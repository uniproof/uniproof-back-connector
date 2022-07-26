package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Wallet {
    private Long id;
    private String ownerType;
    private String ownerId;

    private BigDecimal balance;
    private BigDecimal provisionedBalance;
    private BigDecimal credit;

    private Date createdAt;
    private Date updatedAt;

    public BigDecimal getCurrentBalance() {
        return balance.subtract(provisionedBalance);
    }
}
