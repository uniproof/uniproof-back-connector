package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Wallet {
    private String ownerType;
    private String ownerId;

    private BigDecimal balance;
    private BigDecimal provisionedBalance;
    private BigDecimal currentBalance;
    private BigDecimal credit;

    private Date createdAt;
    private Date updatedAt;

    public BigDecimal getCurrentBalance() {
        if (currentBalance == null) {
            currentBalance = balance.subtract(provisionedBalance);
        }
        return currentBalance;
    }
}
