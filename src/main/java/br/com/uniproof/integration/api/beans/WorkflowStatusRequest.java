package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo de {@code PUT /notaries/workflow/{lotItemId}}: move o processo para
 * outro status do workflow.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStatusRequest {

    private String status;
    private String description;
    /** Pula a validacao de saldo da carteira na transicao */
    private Boolean skipBalanceValidation;
}
