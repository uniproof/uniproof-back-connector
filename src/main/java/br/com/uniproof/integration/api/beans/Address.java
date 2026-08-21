package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Endereco de um owner (usuario ou empresa), retornado por
 * {@code POST /notaries/addresses}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Address {

    private Long id;
    private String ownerType;
    private Long ownerId;
    private Long cityId;
    private String address;
    private String complement;
    private String district;
    private String number;
    private String zipCode;
    /** Tipo do endereco (ex.: billing, mailing) */
    private String kind;
    private City city;
    private Date createdAt;
    private Date updatedAt;
}
