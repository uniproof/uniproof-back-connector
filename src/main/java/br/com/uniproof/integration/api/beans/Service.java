package br.com.uniproof.integration.api.beans;

import lombok.Data;

@Data
public class Service {
    private Long id;
    private String name;
    private String description;
    private String identifier;
    private String fullDescription;
    private String dataUrl;
    private String editUrl;
    private String viewUrl;
    private String validateUrl;
    private String copyUrl;
    private String helpUrl;
    private Boolean active;
    private Boolean onlyPdf;
    private Boolean requireFile;
    private Boolean requireSignature;
    private Boolean onDemand;
    private String alias;
    private String initialStatus;
}
