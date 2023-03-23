package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.Date;

@Data
public class Document {

    private String id;
    private String containerId;
    private Integer lastEventId;
    private String extension;
    private String mimetype;
    private String name;
    private Integer pages;
    private Boolean signed;
    private Long totalSize;
    private Integer currentVersion;
    private Date archivedAt;
    private Date createdAt;
    private Date updatedAt;
    private User creator;

}
