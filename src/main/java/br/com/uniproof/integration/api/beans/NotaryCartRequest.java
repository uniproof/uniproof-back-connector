package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Corpo de {@code POST /notaries/cart} (envia os processos ao cartorio).
 *
 * <p>Difere do {@link CartConfirmRequest}, usado no {@code POST /api/carts},
 * pelo campo {@link #force}.</p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotaryCartRequest {

    @Builder.Default
    private List<String> ownerId = new ArrayList<>();
    @Builder.Default
    private List<String> ownerType = new ArrayList<>();
    @Builder.Default
    private List<String> lotItemIds = new ArrayList<>();
    @Builder.Default
    private List<String> lotIds = new ArrayList<>();
    /** Envia mesmo com validacoes pendentes */
    private Boolean force;
}
