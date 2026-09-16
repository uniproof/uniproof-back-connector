package br.com.uniproof.integration.api.beans;

import lombok.Getter;

/**
 * Retorno de uma chamada a API Uniproof: <strong>ou</strong> o dado,
 * <strong>ou</strong> o erro.
 *
 * <p>Existe porque a API sinaliza falha com status HTTP de erro e corpo
 * {@code {statusCode, message}} — e o Feign, nesse caso, lanca excecao antes de
 * decodificar o corpo no tipo de retorno. Em vez de obrigar quem chama a
 * capturar {@code UniproofApiException}, os metodos que devolvem
 * {@code ApiResponse} nunca lancam por erro de negocio: o erro vem dentro do
 * proprio objeto.</p>
 *
 * <pre>
 * ApiResponse&lt;Tag&gt; r = notaryService.addTagToLotItem(lotItemId, tagId, token);
 * if (r.isSuccess()) {
 *     Tag tag = r.getData();
 * } else {
 *     log.warn("{}: {}", r.getStatusCode(), r.getErrorMessage());
 * }
 * </pre>
 */
@Getter
public class ApiResponse<T> {

    /** Status HTTP da resposta */
    private final int statusCode;

    /** Corpo da resposta em caso de sucesso; {@code null} em caso de erro */
    private final T data;

    /** Corpo de erro, quando a API devolveu um JSON reconhecivel; senao {@code null} */
    private final ApiError error;

    /** Corpo cru, preenchido quando o erro nao veio em JSON (ex.: pagina HTML de 503) */
    private final String rawError;

    private ApiResponse(int statusCode, T data, ApiError error, String rawError) {
        this.statusCode = statusCode;
        this.data = data;
        this.error = error;
        this.rawError = rawError;
    }

    public static <T> ApiResponse<T> ok(int statusCode, T data) {
        return new ApiResponse<>(statusCode, data, null, null);
    }

    public static <T> ApiResponse<T> fail(UniproofApiException e) {
        return new ApiResponse<>(e.getStatusCode(), null, e.getApiError(), e.getRawBody());
    }

    /** A chamada foi bem-sucedida? */
    public boolean isSuccess() {
        return error == null && rawError == null;
    }

    /**
     * Mensagem do erro pronta para log ou para mostrar ao usuario. Cai no corpo
     * cru quando a API nao devolveu JSON. {@code null} em caso de sucesso.
     */
    public String getErrorMessage() {
        if (error != null && error.messageAsText() != null) {
            return error.messageAsText();
        }
        return rawError;
    }
}
