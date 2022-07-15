package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PriceSimulation {
	private BigDecimal sellingPrice;
	private BigDecimal cost;
	private BigDecimal notaryPrice;
	List<Price> composition;
}
