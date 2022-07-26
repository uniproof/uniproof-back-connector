package br.com.uniproof.integration.api.beans;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletCreateRequest {

    private String containerId;
    private BigDecimal credit;

}
