package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Data
public class SignatureDocument {
    private UUID id;
    private UUID documentId;
    private UUID attachmentID;
    private String filename;
    private Long fileSize;
    private String mimeType;
    private String status;
    private Date createdAt;
    private Date updatedAt;
    private Boolean isDeleted;
    private Boolean isConcluded;
    private List<SignatureSigner> signers;
}
