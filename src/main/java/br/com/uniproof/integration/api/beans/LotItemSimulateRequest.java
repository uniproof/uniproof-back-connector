package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LotItemSimulateRequest {
    private Integer serviceId;
    private Integer cityId;
    private Integer documentPages;
    private Integer documentsCount;
    private Object form;
}
