package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.util.List;

@Data
public class Folder {
    private String id;
    private Integer containerTypeId;
    private String name;
    private String description;
    private String parentId;
    private Integer parentDepth;
    private List<String> parentIds;
    private String icon;
    private String url;
    private String target;
    private String message;
    private Integer statusCode;
}
