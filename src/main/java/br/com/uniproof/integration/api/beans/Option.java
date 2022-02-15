package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import java.io.IOException;
import java.util.Date;

@Data
public class Option {
    private Integer id;

    private String ownerType;

    private String ownerId;
    private String name;

    private String value;
    private String kind;
    private String moduleName;
    private Integer weight;
    private Date createdAt;
    private Date updatedAt;

    @JsonIgnore
    public <T> T getValueAsObject(Class<T> classe) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        return mapper.readValue(value, classe);
    }

}
