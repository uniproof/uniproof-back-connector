package br.com.uniproof.integration.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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

    /**
     * Keeps every call to the API inside its rate limit (see
     * {@link UniproofRateLimiter}). Declared here, not as a component, because
     * some consumers load the back-connector with an explicit {@code @Import}
     * of this class instead of a component scan.
     *
     * <p>Defaults fit the strictest environment (dev and stage allow 25
     * requests per second, prod 50; every environment allows 5 error responses
     * per second).</p>
     */
    @Bean
    public UniproofRateLimiter uniproofRateLimiter(
            @Value("${uniproof.api.rate-limit.enabled:true}") boolean enabled,
            @Value("${uniproof.api.rate-limit.max-requests:20}") int maxRequests,
            @Value("${uniproof.api.rate-limit.max-errors:4}") int maxErrors,
            @Value("${uniproof.api.rate-limit.window-ms:1000}") long windowMs,
            @Value("${uniproof.api.rate-limit.max-retries:10}") int maxRetries) {
        return new UniproofRateLimiter(enabled, maxRequests, maxErrors, windowMs, maxRetries);
    }

}
