package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.Blockchain;
import br.com.uniproof.integration.api.config.UniproofClientConfig;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "admin", url = "${uniproof.api.restUrl}", configuration = UniproofClientConfig.class)
public interface UniproofAdminClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/blockchain/pendent?limit={limit}")
    List<Blockchain> getLotItemsToRegister(
            @RequestHeader("X-Company-Token") String notaryToken,
            @PathVariable("limit") Integer limit
    );

    @RequestMapping(method = RequestMethod.GET, value = "/api/blockchain/confirm?limit={limit}")
    List<Blockchain> getLotItemsToConfirm(
            @RequestHeader("X-Company-Token") String notaryToken,
            @PathVariable("limit") Integer limit
    );

    @RequestMapping(method = RequestMethod.GET, value = "/api/blockchain/registered?limit={limit}")
    List<Blockchain> getLotItemsToRecheck(
            @RequestHeader("X-Company-Token") String notaryToken,
            @PathVariable("limit") Integer limit
    );


    @RequestMapping(method = RequestMethod.PUT, value = "/api/blockchain/{lotItemId}")
    Response putLotItemBlockchainData(
            @PathVariable("lotItemId") String lotItemId,
            @RequestBody() Blockchain blockchain,
            @RequestHeader("X-Company-Token") String notaryToken
    );
}
