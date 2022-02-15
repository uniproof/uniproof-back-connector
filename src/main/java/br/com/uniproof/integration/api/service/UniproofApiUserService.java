package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.client.UniproofUserClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
@Slf4j
public class UniproofApiUserService {

    @Autowired
    UniproofUserClient uniproofApiClient;

    public List<LotItem> getLotDocuments(String lotId, String companyToken) {
        return uniproofApiClient.listLotInfo(lotId, companyToken);
    }

    public MultipartFile[] getDocument(String documentId, String token) {
        return uniproofApiClient.getDocument(documentId, token);
    }

    public List<br.com.uniproof.integration.api.beans.Service> getServices(String companyToken) {
        return uniproofApiClient.getServices(companyToken);
    }

    public List<Company> getCompany() {
        return uniproofApiClient.getCompanyResult();
    }


    public Lot createLot(Lot lot, String companyToken) {
        return uniproofApiClient.createLot(lot, companyToken);
    }

    public LotItem uploadFileToLot(
            String lotId,
            String name,
            Path file,
            String companyToken) {
        LotItem result = null;
        try {
            MultipartFile mpfile = new MockMultipartFile(
                    "file",
                    name,
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                    Files.newInputStream(file));

            result = uniproofApiClient.uploadFileToLot(lotId, mpfile, companyToken).getBody();
        } catch (FeignException f) {
            throw f;
        } catch (Exception ex) {
            log.error("Falha ao criar arquivo na plataforma: ", ex);
            throw new RuntimeException(ex);
        }
        return result;
    }

    public void confirmLotSend(
            List<String> lotIds,
            String companyToken) {

        ResponseEntity<List<CartItem>> retorno =
                uniproofApiClient.confirmSend(
                        CartConfirmRequest.builder().lotIds(lotIds).build(),
                        companyToken);

        /*
        StringBuffer jsonlotids = new StringBuffer();
        jsonlotids.append("{ \"lotIds\": [ ");
        lotIds.forEach(id -> {
            jsonlotids.append("\"")
                    .append(id)
                    .append("\",");
        });
        jsonlotids.deleteCharAt(jsonlotids.length() - 1);
        jsonlotids.append("]}");
        ResponseEntity<String> retorno = uniproofApiClient.confirmLotSend(jsonlotids.toString(), companyToken);
        return;

         */
    }

}
