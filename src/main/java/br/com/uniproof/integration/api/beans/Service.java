package br.com.uniproof.integration.api.beans;

import lombok.Data;

@Data
public class Service {
    private Long id;
    private String name;
    private String description;
    private String fullDescription;
    private String dataUrl;
    private String editUrl;
    private String viewUrl;
    private String validateUrl;
    private Boolean requireFile;
    private Boolean requireSignature;
    private Boolean onDemand;
    private String alias;
}
