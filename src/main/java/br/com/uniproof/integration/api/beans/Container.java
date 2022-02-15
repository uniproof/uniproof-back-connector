package br.com.uniproof.integration.api.beans;

import lombok.Data;

@Data
public class Container {

    private String id;
    private Integer containerTypeId;
    private String name;
    private String description;

}
