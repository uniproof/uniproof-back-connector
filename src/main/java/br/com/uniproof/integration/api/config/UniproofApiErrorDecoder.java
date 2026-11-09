package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.beans.ApiError;
import br.com.uniproof.integration.api.beans.UniproofApiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RetryableException;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Traduz as respostas de erro da API Uniproof em {@link UniproofApiException},
 * com o corpo {@code {statusCode, message, error}} ja interpretado.
 *
 * <p>Sem isto, o {@code ErrorDecoder} padrao do Feign lanca uma
 * {@code FeignException} generica e o corpo fica acessivel apenas como bytes em
 * {@code responseBody()} — era por isso que o codigo precisava interpretar o
 * JSON na mao para saber, por exemplo, que um 409 de upload significava anexo
 * duplicado.</p>
 *
 * <p>Corpos que nao sao JSON reconhecivel (por exemplo a pagina HTML de 503 que
 * o Heroku devolve quando a aplicacao esta reiniciando) nao quebram nada: ficam
 * disponiveis crus em {@link UniproofApiException#getRawBody()}.</p>
 *
 * <p><strong>Rate limit:</strong> every error response from the API is
 * counted in the {@link UniproofRateLimiter} error budget, and a 429 becomes a
 * {@link RetryableException} (status 429, with the original
 * {@link UniproofApiException} as cause and the time from {@code Retry-After})
 * so that {@link UniproofRetryer} sends the request again. Responses from other
 * hosts (e.g. the Lacuna signature service) do not touch the limiter.</p>
 */
@Slf4j
public class UniproofApiErrorDecoder implements ErrorDecoder {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private final UniproofRateLimiter rateLimiter;
    private final String apiUrl;

    /** Without a rate limiter: errors are only translated, never retried. */
    public UniproofApiErrorDecoder() {
        this(null, null);
    }

    /**
     * @param apiUrl base URL of the Uniproof API; only its responses count
     *               against the rate limit (null counts every response)
     */
    public UniproofApiErrorDecoder(UniproofRateLimiter rateLimiter, String apiUrl) {
        this.rateLimiter = rateLimiter;
        this.apiUrl = apiUrl;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        byte[] corpo = lerCorpo(response);
        String texto = corpo != null ? new String(corpo, StandardCharsets.UTF_8) : null;
        ApiError erro = interpretar(texto);
        UniproofApiException excecao = new UniproofApiException(
                response.status(), erro, texto, methodKey,
                response.request(), corpo, response.headers());
        log.debug("Erro da API Uniproof em [{}]: {}", methodKey, excecao.getMessage());

        if (rateLimiter == null || !daApiUniproof(response)) {
            return excecao;
        }
        long retryAt = rateLimiter.retryAt(nome -> primeiroHeader(response, nome));
        rateLimiter.recordError(response.status(), retryAt);
        if (response.status() == UniproofRateLimiter.TOO_MANY_REQUESTS && response.request() != null) {
            return new RetryableException(response.status(), excecao.getMessage(),
                    response.request().httpMethod(), excecao, retryAt, response.request());
        }
        return excecao;
    }

    /**
     * O corpo do Feign e um stream de uso unico: le tudo aqui e reaproveita os
     * mesmos bytes na mensagem, na interpretacao e no
     * {@code FeignException.content()}.
     */
    private byte[] lerCorpo(Response response) {
        if (response.body() == null) {
            return null;
        }
        try (InputStream is = response.body().asInputStream()) {
            return is.readAllBytes();
        } catch (IOException e) {
            log.warn("Nao foi possivel ler o corpo do erro da API Uniproof: {}", e.getMessage());
            return null;
        }
    }

    private boolean daApiUniproof(Response response) {
        return apiUrl == null || response.request() == null || response.request().url().startsWith(apiUrl);
    }

    private static String primeiroHeader(Response response, String nome) {
        if (response.headers() == null) {
            return null;
        }
        return response.headers().entrySet().stream()
                .filter(h -> h.getKey() != null && h.getKey().equalsIgnoreCase(nome))
                .flatMap(h -> h.getValue().stream())
                .findFirst()
                .orElse(null);
    }

    /** Devolve {@code null} quando o corpo nao e um objeto JSON de erro. */
    private ApiError interpretar(String corpo) {
        if (corpo == null || corpo.isBlank() || !corpo.stripLeading().startsWith("{")) {
            return null;
        }
        try {
            ApiError erro = MAPPER.readValue(corpo, ApiError.class);
            // Corpo JSON sem nenhum campo conhecido nao serve de erro estruturado.
            return (erro.getMessage() != null || erro.getStatusCode() != null || erro.getError() != null)
                    ? erro : null;
        } catch (Exception e) {
            return null;
        }
    }
}
