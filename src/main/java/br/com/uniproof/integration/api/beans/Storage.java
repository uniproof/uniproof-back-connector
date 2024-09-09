package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Storage {

    private Long id;
    private String documentId;
    private Integer version;
    private String name;
    private String size;
    private Integer pages;
    private String sha256;
    private String doublesha256;
    private List<Signer> signers = new ArrayList<>();
}
