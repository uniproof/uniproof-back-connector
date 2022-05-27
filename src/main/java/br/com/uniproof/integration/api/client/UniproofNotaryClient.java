package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.config.UniproofClientConfig;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(name = "notaries", url = "${uniproof.api.restUrl}", configuration = UniproofClientConfig.class)
public interface UniproofNotaryClient {

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/download")
    Response getDocumentContentById(
            @PathVariable("id") String documentId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/download?version=1")
    Response getDocumentOriginalContentById(
            @PathVariable("id") String documentId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/download?version={version}")
    Response getDocumentVersionContentById(
            @PathVariable("id") String documentId,
            @PathVariable("version") Integer version,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}")
    Lot getLotById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}/json_data")
    String getLotJsonById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/notaries/lots/{id}/json_data")
    @Headers("Content-Type: application/json")
    Object setLotJsonObjectById(
            @PathVariable("id") String lotId,
            @RequestBody Object form,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}/json_data")
    Object getLotJsonObjectById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/{id}")
    LotItem getLotItemById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/sha256/{sha256}")
    List<LotItem> getLotItemBySha256(
            @PathVariable("sha256") String sha256,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/{id}/events")
    List<Event> getLotItemEventsById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String companyToken
    );


    @RequestMapping(method = RequestMethod.PUT, value = "/notaries/lot_items/{id}/protocol", consumes = "application/json")
    String setProtocolOnLotItemById(
            @PathVariable("id") String lotId,
            @RequestHeader("X-Company-Token") String companyToken,
            @RequestBody(required = true) String protocol
    );

    @PostMapping(value = "/notaries/lot_items/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<LotItem> uploadFileToLotItem(
            @PathVariable("id") String lotItemId,
            @RequestPart(value = "file", required = true) MultipartFile file,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @PostMapping(value = "/notaries/documents")
    ResponseEntity<DocumentRequest> linkLocationToDocument(
            @RequestBody DocumentRequest documentRequest,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @PostMapping(value = "/notaries/lot_items/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LotItem> uploadAttachmentParentToLotItem(
            @PathVariable("id") String lotItemId,
            @PathVariable(value = "file", required = true) MultipartFile file,
            @PathVariable(value = "attachmentTypeId", required = true) Integer attachmentTypeId,
            @PathVariable(value = "parentId", required = false) String parentId,
            @RequestHeader("X-Company-Token") String companyToken
    );
    @PostMapping(value = "/notaries/lot_items/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LotItem> uploadAttachmentToLotItem(
            @PathVariable("id") String lotItemId,
            @PathVariable(value = "file", required = true) MultipartFile file,
            @PathVariable(value = "attachmentTypeId", required = true) Integer attachmentTypeId,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @PutMapping(value = "/notaries/attachments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Attachment> updateAttachmentType(
            @PathVariable("id") String lotItemId,
            @RequestBody AttachmentTypeRequest body,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @DeleteMapping(value = "/notaries/lot_items/{id}/attachments/{attId}", produces = MediaType.APPLICATION_JSON_VALUE )
    ResponseEntity<LotItem> deleteAttachmentFromLotItem(
            @PathVariable("id") String lotItemId,
            @PathVariable("attId") String attachmentId,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/notaries/events")
    ResponseEntity postNewEvent(
            Event evento,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/recipients/{owner_type}/{owner_id}")
    List<User> getRecipientsById(
            @PathVariable("owner_type") String ownerType,
            @PathVariable("owner_id") String ownerId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/prices/{id}")
    List<Price> getPriceById(
            @PathVariable("id") String lotItemId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.PUT, value = "/notaries/prices/{id}")
    Response setPriceById(
            @PathVariable("id") String lotItemId,
            @RequestBody List<Price> prices,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}")
    Document getDocumentById(
            @PathVariable("id") String documentId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/{version}/storage")
    Storage getStorageByDocumentIdAndVersion(
            @PathVariable("id") String documentId,
            @PathVariable("version") Integer version,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/{type}/{id}")
    Company getOwner(
            @PathVariable("type") String ownerType,
            @PathVariable("id") String ownerId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/user/{id}")
    User getUser(
            @PathVariable("id") String userId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/company/{id}")
    Company getCompany(
            @PathVariable("id") String companyId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/options")
    List<Option> getOptions(
            @RequestParam(value = "ownerType", required = false) String ownerType,
            @RequestParam(value = "ownerId", required = false) Long ownerId,
            @RequestParam(value = "moduleName", required = false) String moduleName,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/notaries/services/{serviceId}")
    Service getLotById(
            @PathVariable("serviceId") Integer serviceIdId,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets")
    Wallet createContainerWallet(
            @RequestBody WalletRequest walletRequest,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets/credit")
    Wallet changeLimitWallet(
            @RequestBody WalletRequest walletRequest,
            @RequestHeader("X-Company-Token") String notaryToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets/balance")
    Wallet addBalanceLotItemWallet(
            @RequestBody WalletRequest walletRequest,
            @RequestHeader("X-Company-Token") String notaryToken
    );

}
