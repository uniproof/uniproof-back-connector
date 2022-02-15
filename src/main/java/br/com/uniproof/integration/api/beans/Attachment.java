package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.Date;

@Data
public class Attachment {
    private String id;
    private String ownerType;
    private String ownerId;
    private String parentId;
    private Long currentVersion;
    private Date removedAt;
    private Date createdAt;
    private Date updatedAt;
    private Document document;
    private AttachmentType attachmentType;
}
