package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Lot {

    private String id;
    private String ownerType;
    private Long ownerId;
    private Long lastEventId;
    private Long serviceId;
    private String containerId;
    private Long notaryId;
    private Long cityId;
    private Long priceId;
    private String name;
    private BigDecimal price;
    private String description;
    private Event event;
    private List<LotItem> lotItems;
}
