package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.Date;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Owner {
    private Long id;
    private String token;
    private String kind;
    private String name;
    private String fantasy;
    private String phone;
    private String shortName;
    private String cpf;
    private String cnpj;
    private String email;
    private Boolean onlyCommercialAddress;
    private Date endContractTerm;
    private Date startContractTerm;
    private String comments;
}
