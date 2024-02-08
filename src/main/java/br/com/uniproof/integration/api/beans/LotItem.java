package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LotItem {

    private String id;
    private String lotId;
    private String documentId;
    private Integer currentVersion;
    private Integer registeredVersion;
    private Integer lastEventId;
    private String notaryUserId;
    private Long notaryId;
    private String protocol;
    private String archiverId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date archivedAt;
    private Date startedAt;
    private BigDecimal sellingPrice;
    private BigDecimal notaryPrice;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date updatedAt;
    private Document document;
    private List<Price> composition;
    private Event event;
    private Owner owner;
    private Service service;
    private City city;
    private List<Attachment> attachments;
    private Lot lot;
    private Container container;
    private Blockchain blockchain;
    private Boolean hasNotification;
    private String parentId;

}
