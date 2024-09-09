package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Container {

    private String id;
    private Integer containerTypeId;
    private String name;
    private String description;
    private String externalAppId;
    private String ownerType;
    private Long ownerId;
    private Long sequentialId;
    private String parentId;
    private Long parentDepth;
    private Boolean deleted;
    private String icon;
    private String url;
    private String target;
    private Long sequence;
    private String tooltip;

}
