package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private Long id;
    private Long userId;
    private String ownerType;
    private String ownerId;
    private String status;
    private String description;
    private Boolean manual;
    private Long previousStatusId;
    private Long previousEventId;
    private String lotItemId;
    private BigDecimal value;
    @Builder.Default
    private Boolean skipBalanceValidation = Boolean.FALSE;
    private Date createdAt;
    private Date updatedAt;

    Event(Long id) {
        this.id = id;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
        lotItemId = ownerId;
    }

}
