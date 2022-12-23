package br.com.uniproof.integration.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class UniproofApiConfig {

    @Value("${spring.profiles.active:Unknown}")
    private String activeProfile;

    @Value("${uniproof.api.notary}")
    private String notary;

    @Value("${uniproof.api.admin}")
    private String admin;

    @Value("${uniproof.api.restUrl}")
    private String restUrl;

    @Value("${uniproof.api.login.email}")
    private String loginEmail;

    @Value("${uniproof.api.login.pass}")
    private String loginPass;

    @Value("${uniproof.api.serviceUrl}")
    private String serviceApiUrl;

}