package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.SignatureDocument;
import br.com.uniproof.integration.api.client.UniproofSignatureClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class UniproofApiSignatureService {

    @Autowired
    UniproofSignatureClient uniproofSignatureClient;

    public List<SignatureDocument> getSignaturesFromLotItem(
            String lotItemId,
            String companyToken) {
        return uniproofSignatureClient.getSignaturesFromLotItem(lotItemId).getBody();
    }

}
