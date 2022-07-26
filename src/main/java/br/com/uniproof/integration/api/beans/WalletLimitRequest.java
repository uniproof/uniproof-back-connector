package br.com.uniproof.integration.api.beans;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletLimitRequest {

    private String lotItemId;
    private BigDecimal credit;
    private String observation;

}
