package br.com.uniproof.integration.api.beans;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
public class WalletRequest {
    private String ownerType;
    private String ownerId;
    private String lotItemId;
    private String containerId;

    private BigDecimal credit;
    private BigDecimal balance;

    private String observation;
}
