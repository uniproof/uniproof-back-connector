package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.config.UniproofClientConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(name = "api", url = "${uniproof.api.restUrl}", configuration = UniproofClientConfig.class)
@Component
public interface UniproofUserClient {

    @RequestMapping(method = RequestMethod.GET, value = "/api/companies")
    List<Company> getCompanyResult();

    @RequestMapping(method = RequestMethod.GET, value = "/api/cities")
    List<City> listCities(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestHeader("X-Company-Token") String companyToken);


    @RequestMapping(method = RequestMethod.GET, value = "/api/lots/{lotId}/documents")
    List<LotItem> listLotInfo(
            @PathVariable("lotId") String lotId,
            @RequestHeader("X-Company-Token") String companyToken);


    @PostMapping(value = "/api/lots/{lotId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LotItem> uploadFileToLot(
            @PathVariable("lotId") String lotId,
            @PathVariable(value = "file", required = true) MultipartFile file,
            @RequestHeader("X-Company-Token") String companyToken
    );


    @RequestMapping(method = RequestMethod.GET, value = "/api/lots/{lotId}/documents/{lotItemId}")
    LotItem getLotItemInfo(
            @PathVariable("lotId") String lotId,
            @PathVariable("lotItemId") String lotItemId,
            @RequestHeader("X-Company-Token") String companyToken);


    @RequestMapping(method = RequestMethod.DELETE, value = "/api/lots/{lotId}/documents/{lotItemId}")
    LotItem removeLotItemFromLot(
            @PathVariable("lotId") String lotId,
            @PathVariable("lotItemId") String lotItemId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.POST, value = "/api/lots/{lotId}/documents/{lotItemId}/retry")
    String resolveRequirement(
            @PathVariable("lotId") String lotId,
            @PathVariable("lotItemId") String lotItemId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.POST, value = "/api/lots")
    Lot createLot(
            @RequestBody Lot lot,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.PUT, value = "/api/lots/{lotId}")
    Lot updateLot(
            @PathVariable("lotId") String lotId,
            @RequestBody Lot lot,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/api/lots/{lotId}/forms")
    FormResponse fillFormJson(
            @PathVariable("lotId") String lotId,
            @RequestBody String jsonForm,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.POST, value = "/api/lots/{lotId}/forms")
    @Headers("Content-Type: application/json")
    FormResponse fillForm(
            @PathVariable("lotId") String lotId,
            @RequestBody Object form,
            @RequestHeader("X-Company-Token") String companyToken
    );


    @RequestMapping(method = RequestMethod.GET, value = "/api/folders")
    List<Folder> listFolders(
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/folders")
    List<Folder> listFolders(
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.POST, value = "/api/folders")
    Folder createFolder(
            @RequestBody Folder folder,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/folders/lots")
    List<Lot> listLotsFromFolder(
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/folders/{folderId}")
    Folder getFolder(
            @PathVariable("folderId") String folderId,
            @RequestHeader("X-Company-Token") String companyToken);


    @RequestMapping(method = RequestMethod.PUT, value = "/api/folders/{folderId}")
    Folder updateFolder(
            @PathVariable("folderId") String folderId,
            @RequestBody Folder folder,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/folders/{folderId}")
    Folder removeFolder(
            @PathVariable("folderId") String folderId,
            @RequestHeader("X-Company-Token") String companyToken);



    @RequestMapping(method = RequestMethod.GET, value = "/api/documents/{documentId}/download")
    MultipartFile[] getDocument(
            @PathVariable("documentId") String documentId,
            @RequestHeader() String token
    );

    @RequestMapping(method = RequestMethod.GET, value = "/api/documents/{documentId}/{documentVersion}/storage")
    MultipartFile[] getDocumentVersion(
            @PathVariable("documentId") String documentId,
            @PathVariable("documentVersion") String documentVersion,
            @RequestHeader() String token
    );

    @RequestMapping(method = RequestMethod.GET, value = "/api/services")
    List<Service> getServices(
            @RequestHeader("X-Company-Token") String companyToken);



    @PostMapping(value = "/api/lotItem/{lotItemId}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<LotItem> uploadAttachmentToLotItem(
            @PathVariable("lotItemId") String lotId,
            @PathVariable(value = "file", required = true) MultipartFile file,
            @PathVariable(value = "attachmentTypeId", required = true) Integer attachmentTypeId,
            @RequestHeader("X-Company-Token") String companyToken
    );

    @RequestMapping(method = RequestMethod.GET, value = "/api/attachment_types")
    List<AttachmentType> listAttachmentType(
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/event_types")
    List<EventType> listEventType(
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/wallets", consumes = "application/json")
    ResponseEntity<Wallet> getWalletBalance(
            @RequestParam(value = "containerId", required = false) String containerId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/carts", consumes = "application/json")
    ResponseEntity<List<CartItem>> getCart(
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.GET, value = "/api/carts/{containerId}", consumes = "application/json")
    ResponseEntity<List<CartItem>> getCartFromFolder(
            @PathVariable("containerId") String containerId,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.POST, value = "/api/carts", consumes = "application/json")
    ResponseEntity<String> confirmLotSend(
            @RequestBody String lotIds,
            @RequestHeader("X-Company-Token") String companyToken);

    @RequestMapping(method = RequestMethod.POST, value = "/api/carts", consumes = "application/json")
    ResponseEntity<List<CartItem>> confirmSend(
            @RequestBody CartConfirmRequest lotItemIds,
            @RequestHeader("X-Company-Token") String companyToken);
}
