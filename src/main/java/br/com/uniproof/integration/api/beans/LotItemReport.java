package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Resultado de {@code GET /notaries/lot_items/report}: pagina de linhas do
 * relatorio mais a contagem total.
 *
 * <p>As linhas vem de uma projecao SQL montada dinamicamente conforme os
 * filtros, e nao de uma entidade fixa — por isso sao mapeadas como
 * {@code Map} em vez de um bean com campos fixos.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotItemReport {

    private List<Map<String, Object>> results;
    /** Total de registros que atendem ao filtro, ignorando limit/offset */
    private Long count;
}
