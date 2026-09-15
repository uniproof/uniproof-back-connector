package br.com.uniproof.integration.api.beans;

import feign.FeignException;
import feign.Request;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;

/**
 * Erro devolvido pela API Uniproof, com o corpo ja interpretado.
 *
 * <p>Antes, o {@code ErrorDecoder} padrao do Feign lancava uma
 * {@code FeignException} generica e o corpo ficava acessivel apenas como bytes:
 * quem quisesse saber o motivo tinha de ler {@code responseBody()} e interpretar
 * o JSON na mao. Aqui o status e a mensagem chegam prontos.</p>
 *
 * <p><strong>Estende {@link FeignException} de proposito:</strong> ha codigo em
 * producao — no proprio back-connector e no central-br-connector — que faz
 * {@code catch (FeignException)} ou {@code instanceof FeignException}. Herdar
 * mantem esses tratamentos funcionando; quem quiser os campos interpretados
 * captura {@code UniproofApiException}.</p>
 */
@Getter
public class UniproofApiException extends FeignException {

    /** Corpo de erro interpretado; {@code null} quando nao era JSON reconhecivel */
    private final ApiError apiError;

    /** Corpo cru da resposta (o mesmo de {@link #contentUTF8()}), para conveniencia */
    private final String rawBody;

    /** Metodo do client que originou a chamada (formato do Feign) */
    private final String methodKey;

    public UniproofApiException(int status,
                               ApiError apiError,
                               String rawBody,
                               String methodKey,
                               Request request,
                               byte[] corpo,
                               Map<String, Collection<String>> headers) {
        super(status, montarMensagem(status, apiError, rawBody, methodKey), request, corpo, headers);
        this.apiError = apiError;
        this.rawBody = rawBody;
        this.methodKey = methodKey;
    }

    /** Status HTTP da resposta (mesmo valor de {@link #status()}). */
    public int getStatusCode() {
        return status();
    }

    /** A mensagem de negocio devolvida pela API, quando houver. */
    public String getApiMessage() {
        return apiError != null ? apiError.messageAsText() : null;
    }

    private static String montarMensagem(int status, ApiError apiError, String rawBody, String methodKey) {
        String detalhe = apiError != null && apiError.messageAsText() != null
                ? apiError.messageAsText()
                : abreviar(rawBody);
        return "API Uniproof respondeu " + status + " em [" + methodKey + "]"
                + (detalhe != null ? ": " + detalhe : "");
    }

    private static String abreviar(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        String limpo = texto.strip();
        return limpo.length() <= 500 ? limpo : limpo.substring(0, 500) + "...";
    }
}
