package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Company extends Owner {
    private Long id;
    private String token;
    private String kind;
    private String name;
    private String fantasy;
    private String phone;
    private String shortName;
    private String cnpj;
    private String cpf;
    private String email;
    private Boolean onlyComercialAddress;
    private Date startContractTerm;
    private Date endContractTerm;
    private String comments;
    //private Set<Long> notaryIds;

    private Map<String, String> properties = new HashMap<>();

    @JsonAnySetter
    public void setProperties(String key, String value) {
        properties.put(key, value);
    }
}
