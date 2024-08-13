package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Signer {
    private String cnpj;
    private String name;
    private String email;
    private String cpfCnpj;
    private String issuerName;
    private String companyName;
    private Date signingTime;
    private String type;
    private String phone;
    private String title;
    private String status;
    private String externalId;
    private List<String> authenticationTypes = new ArrayList<>();
}
