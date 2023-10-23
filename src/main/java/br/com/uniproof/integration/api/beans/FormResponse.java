package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import lombok.Data;

import java.io.IOException;

@Data
public class FormResponse {
    private String data;
    private Boolean valid;

    @JsonIgnore
    public <T> T getDataAsObject(Class<T> formClass) throws IOException {
        ObjectMapper mapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper.readValue(this.data, formClass);
    }

    @JsonIgnore
    public <T> T readDataAttribute(String attibuteToRead, Class<T> returnClass) {
        //"$.data.dados_solicitacao.email_cliente"
        return JsonPath.parse(data).read(attibuteToRead, returnClass);
    }
}
