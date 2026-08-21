package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Corpo de erro da API Uniproof.
 *
 * <p>Formatos observados no ambiente de stage:</p>
 * <pre>
 * {"statusCode":422,"message":"Tag já adicionada"}
 * {"statusCode":404,"message":"Não é possivel acessar o processo"}
 * {"message":"Cannot GET /notaries/x","error":"Not Found","statusCode":404}
 * </pre>
 *
 * <p>{@link #message} e {@code Object} de proposito: o backend e NestJS, que
 * devolve string na maioria dos casos mas lista de strings quando o erro vem da
 * validacao de DTO. Use {@link #messageAsText()} para obter sempre um texto.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiError {

    /** Codigo repetido no corpo; normalmente igual ao status HTTP da resposta */
    private Integer statusCode;

    /** String ou lista de strings — ver {@link #messageAsText()} */
    private Object message;

    /** Nome do erro (ex.: {@code Not Found}); ausente em vários casos */
    private String error;

    /**
     * A mensagem como texto, unindo os itens por {@code "; "} quando o backend
     * devolve uma lista. Devolve {@code null} quando nao ha mensagem.
     */
    public String messageAsText() {
        if (message == null) {
            return null;
        }
        if (message instanceof List<?> lista) {
            return lista.stream()
                    .filter(java.util.Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.joining("; "));
        }
        return message.toString();
    }
}
