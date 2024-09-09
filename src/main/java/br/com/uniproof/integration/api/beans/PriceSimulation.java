package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceSimulation {
    List<Price> composition;
    private BigDecimal sellingPrice;
    private BigDecimal cost;
    private BigDecimal notaryPrice;
}
