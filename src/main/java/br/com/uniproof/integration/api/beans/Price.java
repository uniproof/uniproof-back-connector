package br.com.uniproof.integration.api.beans;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Price {

    private Long id;

    private String name;

    private String behavior;

    private Integer sequence;

    private Boolean percentage;

    private Boolean use;

    private Boolean visible;

    private Boolean editable;

    private Boolean notaryPrice;

    private BigDecimal currentValue;

    private BigDecimal value;

    private BigDecimal originalValue;

    private BigDecimal referenceValue;

}

