package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Tag de processo. Os campos visuais (icone e cores) foram acrescentados apos
 * conferir o retorno real de {@code GET /notaries/tags} e
 * {@code POST /notaries/lot_items/{id}/tags}, que devolvem 11 campos.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tag {

    private String id;
    private String label;
    private String tooltip;
    private Boolean visible;
    private Boolean notary;
    /** Nome do icone (ex.: {@code rotate-exclamation}) */
    private String icon;
    /** Cor do icone/texto, no formato devolvido pela API (ex.: {@code rgb(245, 166, 35)}) */
    private String color;
    private String backgroundColor;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;
}
