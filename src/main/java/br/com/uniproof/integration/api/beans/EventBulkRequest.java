package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Corpo de {@code POST /notaries/events/bulk}: cria o mesmo evento em varios
 * processos de uma vez.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventBulkRequest {

    @Builder.Default
    private List<String> lotItemIds = new ArrayList<>();
    /** Status do workflow a registrar */
    private String status;
    private String description;
}
