package br.com.uniproof.integration.api.beans;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventType {
    private String id;
    private String name;
    private String label;
    private Boolean active;
    private Boolean visible;
}
