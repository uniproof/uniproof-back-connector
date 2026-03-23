package br.com.uniproof.integration.api.beans;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Tag {

    private String id;
    private String label;
    private String tooltip;
    private Boolean visible;
    private Boolean notary;
}
