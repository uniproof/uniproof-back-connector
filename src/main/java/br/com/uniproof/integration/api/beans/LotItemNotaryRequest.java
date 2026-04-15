package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotItemNotaryRequest {

    private String companyToken;

    private Integer serviceId;
    private String containerId;
    private String name;
    private String description;
    private Integer cityId;
    private String parentId;
    private Object form;

}
