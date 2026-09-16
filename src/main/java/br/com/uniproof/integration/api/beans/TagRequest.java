package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo de {@code POST /notaries/lot_items/{id}/tags}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {

    private String tagId;
}
