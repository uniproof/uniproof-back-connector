package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class Blockchain {
    @JsonIgnore
    private String id;
    //@JsonProperty(value = "lot_item_id")
    private String lotItemId;
    private String doublesha256;
    @JsonInclude
    private String protocol;
    private String validationCode;
    private String notaryName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date registryDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private Date confirmationDate;
    private Date createdAt;
    private Date updatedAt;
    private Integer currentVersion;
    private String sha256;
    private String documentId;
}
