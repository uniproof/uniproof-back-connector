package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.config.UniproofClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "signature", url = "${uniproof.api.lacunaUrl:localhost}", configuration = UniproofClientConfig.class)
public interface UniproofSignatureClient {


	@GetMapping(value = "/api/documents/{lotItemid}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<SignatureDocument>> getSignaturesFromLotItem(
			@PathVariable("lotItemid") String lotItemId
	);
}
