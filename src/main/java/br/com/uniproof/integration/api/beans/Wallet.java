package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class Wallet {
    private String ownerType;
    private String ownerId;

    private BigDecimal currentBalance;
    private BigDecimal credit;
    private BigDecimal balance;

    private Date createdAt;
    private Date updatedAt;
}
