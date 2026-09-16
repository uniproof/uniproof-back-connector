package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.beans.ApiError;
import br.com.uniproof.integration.api.beans.UniproofApiException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * <p><strong>Retry:</strong> o decoder padrao so produzia
 * {@code RetryableException} quando a resposta trazia o header
 * {@code Retry-After}, que esta API nao envia. O comportamento efetivo de
 * retentativa, portanto, nao muda com esta classe.</p>
 */
@Slf4j
public class UniproofApiErrorDecoder implements ErrorDecoder {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public Exception decode(String methodKey, Response response) {
        byte[] corpo = lerCorpo(response);
        String texto = corpo != null ? new String(corpo, StandardCharsets.UTF_8) : null;
        ApiError erro = interpretar(texto);
        UniproofApiException excecao = new UniproofApiException(
                response.status(), erro, texto, methodKey,
                response.request(), corpo, response.headers());
        log.debug("Erro da API Uniproof em [{}]: {}", methodKey, excecao.getMessage());
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
