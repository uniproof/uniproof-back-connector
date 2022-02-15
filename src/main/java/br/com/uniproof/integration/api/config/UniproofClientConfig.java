package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTParser;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import feign.Logger;
import feign.RequestInterceptor;
import feign.Retryer;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

import java.text.ParseException;
import java.util.Date;

import static java.util.concurrent.TimeUnit.SECONDS;

@Slf4j
public class UniproofClientConfig {

	@Autowired
	private UniproofApiConfig uniproofApiConfig;

	@Autowired
	private UniproofApiCoreService uniproofCoreApiService;

	private String token = "";

	private Date tokenExpiration = new Date();


	@Autowired
	private ObjectFactory<HttpMessageConverters> messageConverters;

	@Bean
	Logger.Level feignLoggerLevelBackConnector() {
		return Logger.Level.BASIC;
	}

	@Bean
	public Retryer retryerBackConnector() {
		return new Retryer.Default(100, SECONDS.toMillis(10), 10);
	}

	@Bean
	public Encoder feignFormEncoderBackConnector() {
		return new SpringFormEncoder(new SpringEncoder(messageConverters));
	}

	@Bean
	public RequestInterceptor requestInterceptorBackConnector() {
		return requestTemplate -> {
			requestTemplate.header("Authorization", "Bearer " + getToken());
		};
	}

	public String getToken() {
		if (tokenExpiration.before(new Date())) {
			token = uniproofCoreApiService.getToken(uniproofApiConfig.getLoginEmail(), uniproofApiConfig.getLoginPass());

			try {
				JWT jwt = JWTParser.parse(token);
				//Header header = jwt.getHeader();
				ReadOnlyJWTClaimsSet jwtClaimSet = jwt.getJWTClaimsSet();
				Date sync = new Date();
				long iat = jwtClaimSet.getIssueTime().getTime();
				long exp = jwtClaimSet.getExpirationTime().getTime();
				long diff = sync.getTime() - iat;

				tokenExpiration.setTime(exp - diff * 50);

			} catch (ParseException parseException) {
			}
		}
		return token;
	}
}
