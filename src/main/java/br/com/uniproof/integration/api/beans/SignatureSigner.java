package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class SignatureSigner {
    private UUID id;
    private String title;
    private String type;
    private String name;
    private String cpfCnpj;
    private String email;
    private String phone;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private String signatureLink;
}
