package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRequest {

    private String id;
    private String name;
    private String documentId;
    private String currentVersion;
    private String locationId;
    private String size;
    private String totalSize;
    private String fileName;
    private String extension;
    private String mimetype;
    private String lotItemId;
    private String attachmentTypeId;
    private String parentId;
    private String containerId;

}
