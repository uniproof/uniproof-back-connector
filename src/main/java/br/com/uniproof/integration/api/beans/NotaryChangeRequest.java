package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo de {@code PUT /notaries/lot_items/{id}/notary}: troca a serventia
 * responsavel pelo processo.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotaryChangeRequest {

    private String notaryId;
    private String comment;
    /** Evento de workflow a registrar na troca */
    private String event;
}
