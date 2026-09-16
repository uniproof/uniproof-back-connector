package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Corpo de {@code POST /notaries/addresses} (cria ou atualiza o endereco).
 * Todos os campos sao obrigatorios na API.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequest {

    private String ownerType;
    private Long ownerId;
    private Long cityId;
    private String address;
    private String complement;
    private String district;
    private String number;
    private String zipCode;
    private String kind;
}
