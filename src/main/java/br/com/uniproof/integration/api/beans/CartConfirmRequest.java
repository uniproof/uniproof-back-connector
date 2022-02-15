package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CartConfirmRequest {
    @Builder.Default
    private List<String> lotItemIds = new ArrayList<>();
    @Builder.Default
    private List<String> lotIds = new ArrayList<>();
}
