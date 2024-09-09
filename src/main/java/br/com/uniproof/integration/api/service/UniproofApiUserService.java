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

    public List<LotItem> getLotDocuments(
            String lotId,
            String companyToken) {
        return uniproofApiClient.listLotInfo(lotId, companyToken);
    }

    public MultipartFile[] getDocument(
            String documentId,
            String token) {
        return uniproofApiClient.getDocument(documentId, token);
    }

    public List<br.com.uniproof.integration.api.beans.Service> getServices(
            String companyToken) {
        return uniproofApiClient.getServices(companyToken);
    }

    public List<Company> getCompany() {
        return uniproofApiClient.getCompanyResult();
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
    }

    public List<Company> getCompanyResult() {
        return uniproofApiClient.getCompanyResult();
    }

    public List<City> listCities(
            String name,
            Integer offset,
            Integer limit,
            String companyToken) {
        return uniproofApiClient.listCities(name, offset, limit, companyToken);
    }

    public List<LotItem> listLotInfo(
            String lotId,
            String companyToken) {
        return uniproofApiClient.listLotInfo(lotId, companyToken);
    }

    public ResponseEntity<LotItem> uploadFileToLot(
            String lotId,
            MultipartFile file,
            String companyToken) {
        return uniproofApiClient.uploadFileToLot(
                lotId,
                file,
                companyToken);
    }

    public LotItem getLotItemInfo(
            String lotId,
            String lotItemId,
            String companyToken) {
        return uniproofApiClient.getLotItemInfo(
                lotId,
                lotItemId,
                companyToken);
    }

    public LotItem removeLotItemFromLot(
            String lotId,
            String lotItemId,
            String companyToken) {
        return uniproofApiClient.removeLotItemFromLot(
                lotId,
                lotItemId,
                companyToken);
    }

    public String resolveRequirement(
            String lotId,
            String lotItemId,
            String companyToken) {
        return uniproofApiClient.resolveRequirement(
                lotId,
                lotItemId,
                companyToken);
    }

    public Lot createLot(
            Lot lot,
            String companyToken) {
        return uniproofApiClient.createLot(
                lot,
                companyToken
        );
    }

    public Lot updateLot(
            String lotId,
            Lot lot,
            String companyToken) {
        return uniproofApiClient.updateLot(
                lotId,
                lot,
                companyToken
        );
    }

    public FormResponse fillFormJson(
            String lotId,
            String jsonForm,
            String companyToken) {
        return uniproofApiClient.fillFormJson(
                lotId,
                jsonForm,
                companyToken
        );
    }

    public FormResponse fillForm(
            String lotId,
            Object form,
            String companyToken) {
        return uniproofApiClient.fillForm(
                lotId,
                form,
                companyToken
        );
    }


    public List<Folder> listFolders(
            String containerId,
            String companyToken) {
        return uniproofApiClient.listFolders(
                containerId,
                companyToken);
    }

    public List<Folder> listFolders(
            String containerId,
            Integer limit,
            Integer offset,
            String companyToken) {
        return uniproofApiClient.listFolders(
                containerId,
                limit,
                offset,
                companyToken);
    }

    public Folder createFolder(
            Folder folder,
            String companyToken) {
        return uniproofApiClient.createFolder(
                folder,
                companyToken);
    }

    public List<Lot> listLotsFromFolder(
            String containerId,
            String companyToken) {
        return uniproofApiClient.listLotsFromFolder(
                containerId,
                companyToken);
    }

    public Folder getFolder(
            String folderId,
            String companyToken) {
        return uniproofApiClient.getFolder(
                folderId,
                companyToken);
    }


    public Folder updateFolder(
            String folderId,
            Folder folder,
            String companyToken) {
        return uniproofApiClient.updateFolder(
                folderId,
                folder,
                companyToken);
    }

    public Folder removeFolder(
            String folderId,
            String companyToken) {
        return uniproofApiClient.removeFolder(
                folderId,
                companyToken);
    }

    public MultipartFile[] getDocumentVersion(
            String documentId,
            String documentVersion,
            String token) {
        return uniproofApiClient.getDocumentVersion(
                documentId,
                documentVersion,
                token
        );
    }

    public ResponseEntity<LotItem> uploadAttachmentToLotItem(
            String lotId,
            MultipartFile file,
            Integer attachmentTypeId,
            String companyToken) {
        return uniproofApiClient.uploadAttachmentToLotItem(
                lotId,
                file,
                attachmentTypeId,
                companyToken
        );
    }

    public List<AttachmentType> listAttachmentType(
            String companyToken) {
        return uniproofApiClient.listAttachmentType(
                companyToken);
    }

    public LotItem priceSimulate(
            LotItemSimulateRequest lotItemRequest,
            String companyToken
    ) {
        return uniproofApiClient.priceSimulate(lotItemRequest, companyToken);
    }


    public List<EventType> listEventType(
            String companyToken) {
        return uniproofApiClient.listEventType(
                companyToken);
    }

    public ResponseEntity<Wallet> getWalletBalance(
            String containerId,
            String companyToken) {
        return uniproofApiClient.getWalletBalance(
                containerId,
                companyToken);
    }

    public ResponseEntity<List<CartItem>> getCart(
            String companyToken) {
        return uniproofApiClient.getCart(
                companyToken);
    }

    public ResponseEntity<List<CartItem>> getCartFromFolder(
            String containerId,
            String companyToken) {
        return uniproofApiClient.getCartFromFolder(
                containerId,
                companyToken);
    }

    public ResponseEntity<String> confirmLotSend(
            String lotIds,
            String companyToken) {
        return uniproofApiClient.confirmLotSend(
                lotIds,
                companyToken);
    }

    public ResponseEntity<List<CartItem>> confirmSend(
            CartConfirmRequest lotItemIds,
            String companyToken) {
        return uniproofApiClient.confirmSend(
                lotItemIds,
                companyToken);
    }
}
