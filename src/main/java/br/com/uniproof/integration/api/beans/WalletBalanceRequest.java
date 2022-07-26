package br.com.uniproof.integration.api.beans;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WalletBalanceRequest {

    private String lotItemId;
    private String containerId;
    private String companyId;

    private BigDecimal balance;
    private String observation;
}
