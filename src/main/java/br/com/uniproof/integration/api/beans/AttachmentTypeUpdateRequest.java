package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo de {@code PUT /notaries/attachments/{id}/attachment_type}, que troca o
 * tipo do anexo e registra um evento com a justificativa.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttachmentTypeUpdateRequest {

    /** Nome do tipo de anexo (nao o id) */
    private String attachmentTypeName;
    /** Justificativa, registrada no evento gerado */
    private String reason;
}
