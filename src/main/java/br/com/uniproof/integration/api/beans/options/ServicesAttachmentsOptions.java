package br.com.uniproof.integration.api.beans.options;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ServicesAttachmentsOptions {

    private List<ServicesAttachmentsRule> servicesAttachmentsRules;

    private Map<String, Object> serviceSetup;
}
