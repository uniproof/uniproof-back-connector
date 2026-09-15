package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Item de {@code GET /notaries/lot_items/download_list}: um documento
 * pertencente aos processos filtrados.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DownloadListItem {

    /** Nome no formato {@code <lotItemId>-<attachmentId>-<nome do documento>} */
    private String name;
    private String location;
    private String locationId;
}
