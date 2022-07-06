package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import com.jayway.jsonpath.JsonPath;
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

import java.util.Base64;
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

			Base64.Decoder decoder = Base64.getUrlDecoder();
			String[] chunks = token.split("\\.");

			String header = new String(decoder.decode(chunks[0]));
			String payload = new String(decoder.decode(chunks[1]));

			Date sync = new Date();
			long iat = JsonPath.parse(payload).read("$.iat", Long.class)*1000;
			long exp = JsonPath.parse(payload).read("$.exp", Long.class)*1000;
			long diff = sync.getTime() - iat;

			tokenExpiration.setTime(exp - diff * 50);

		}
		return token;
	}
}
