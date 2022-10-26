package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.Token;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
public class UniproofApiCoreService {

    @Autowired
    UniproofApiConfig uniproofApiConfig;


    public String getToken(String login, String Pass) {

        String uri = uniproofApiConfig.getRestUrl() + "/api/auth";
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter()));

        Map<String, String> params = new HashMap<>();
        params.put("email", uniproofApiConfig.getLoginEmail());
        params.put("password", uniproofApiConfig.getLoginPass());

        Token result = restTemplate.postForObject(uri, params, Token.class);

        return result.getToken();
    }


    private HttpHeaders getHeader(String token, String companyToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.add("User-Agent", "Uniproof Java Service");
        headers.add("X-Company-Token", companyToken);
        return headers;
    }


}
