package br.com.uniproof.integration.api.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Papel (role) de uma empresa, retornado por
 * {@code GET /notaries/companies/{companyId}/roles}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Role {

    private Integer id;
    /** Identificador tecnico do papel (ex.: notary, super_notary) */
    private String role;
    /** Nome de exibicao */
    private String name;
    private Integer sortOrder;
    private Boolean active;
    private List<String> modules;
    private Date createdAt;
    private Date updatedAt;
}
