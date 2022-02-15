package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class Token {
    String message;
    String token;
    String name;
    String companyToken;
    Integer termsOfUseVersion;
}
