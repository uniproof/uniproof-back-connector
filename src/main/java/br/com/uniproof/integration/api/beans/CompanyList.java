package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CompanyList {

    private List<Company> tokenResults = new ArrayList<>();

}

