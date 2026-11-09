package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import com.jayway.jsonpath.JsonPath;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.codec.ErrorDecoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.support.FeignHttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import java.util.Base64;
import java.util.Date;


@Slf4j
public class UniproofClientConfig {

    @Autowired
    private UniproofApiConfig uniproofApiConfig;

    @Autowired
    private UniproofApiCoreService uniproofCoreApiService;

    private String token = "";

    private Date tokenExpiration = new Date(0);


    @Autowired
    private ObjectProvider<FeignHttpMessageConverters> messageConverters;

    @Bean
    Logger.Level feignLoggerLevelBackConnector() {
        return Logger.Level.BASIC;
    }

    @Autowired
    private UniproofRateLimiter rateLimiter;

    /** 429 is sent again after the wait the API asked for; I/O failures keep the previous policy. */
    @Bean
    public Retryer retryerBackConnector() {
        return new UniproofRetryer(rateLimiter);
    }

    /**
     * Waits for a slot inside the API rate limit before each attempt (Feign runs
     * the interceptors again on every retry). Other hosts, such as the Lacuna
     * signature service, are not limited.
     */
    @Bean
    public RequestInterceptor rateLimitInterceptorBackConnector() {
        return requestTemplate -> {
            String url = requestTemplate.feignTarget() != null ? requestTemplate.feignTarget().url() : null;
            if (url == null || url.startsWith(uniproofApiConfig.getRestUrl())) {
                rateLimiter.acquire();
            }
        };
    }

    /**
     * Traduz as respostas de erro da API em UniproofApiException, com o corpo
     * {@code {statusCode, message}} interpretado. Herda de FeignException, para
     * nao invalidar os {@code catch (FeignException)} que ja existem.
     */
    @Bean
    public ErrorDecoder errorDecoderBackConnector() {
        return new UniproofApiErrorDecoder(rateLimiter, uniproofApiConfig.getRestUrl());
    }

    @Bean
    public Encoder feignFormEncoderBackConnector() {
        return new SpringFormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public RequestInterceptor requestInterceptorBackConnector() {
        return requestTemplate -> {
            // Chamada "em nome do usuario" (metodos *AsUser do UniproofUserClient) ja traz o
            // token de quem esta na tela; as demais seguem com o token do usuario de servico.
            if (!requestTemplate.headers().containsKey("Authorization")) {
                requestTemplate.header("Authorization", "Bearer " + getToken());
            }
        };
    }

    public String getToken() {
        synchronized (this) {
            try {
                if (tokenExpiration.before(new Date())) {
                    token = uniproofCoreApiService.getToken(uniproofApiConfig.getLoginEmail(), uniproofApiConfig.getLoginPass());

                    Base64.Decoder decoder = Base64.getUrlDecoder();
                    String[] chunks = token.split("\\.");

                    String payload = new String(decoder.decode(chunks[1]));

                    long iat = JsonPath.parse(payload).read("$.iat", Long.class) * 1000;
                    long exp = JsonPath.parse(payload).read("$.exp", Long.class) * 1000;

                    long lifetime = exp - iat;
                    tokenExpiration.setTime(exp - lifetime / 50);
                }
                return token;

            } catch (Exception e) {
                throw new IllegalStateException("Falha ao renovar token Uniproof", e);
            }
        }
    }
}
