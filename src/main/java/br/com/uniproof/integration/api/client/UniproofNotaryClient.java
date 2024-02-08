package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.config.UniproofClientConfig;
import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@FeignClient(name = "notaries", url = "${uniproof.api.restUrl}", configuration = UniproofClientConfig.class)
public interface UniproofNotaryClient {
	//Attachments
	@GetMapping(value = "/notaries/lot_items/{id}/attachments", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<List<Attachment>> getAttachmentFromLotItem(
			@PathVariable("id") String lotItemId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@PostMapping(value = "/notaries/lot_items/{id}/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> uploadAttachmentToLotItem(
			@PathVariable("id") String lotItemId,
			@PathVariable(value = "file", required = true) MultipartFile file,
			@PathVariable(value = "attachmentTypeId", required = true) Integer attachmentTypeId,
			@PathVariable(value = "parentId", required = false) String parentId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@DeleteMapping(value = "/notaries/lot_items/{id}/attachments/{attId}", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> deleteAttachmentFromLotItem(
			@PathVariable("id") String lotItemId,
			@PathVariable("attId") String attachmentId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@PostMapping(value = "/notaries/attachments/duplicate", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> importAttachmentsFromParent(
			@RequestBody AttachmentTypeRequest body,
			@RequestHeader("X-Company-Token") String notaryToken);


	@PostMapping(value = "/notaries/attachments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> uploadAttachmentToOwner(
			@PathVariable(value = "file", required = true) MultipartFile file,
			@PathVariable(value = "attachmentTypeId", required = true) Integer attachmentTypeId,
			@PathVariable(value = "containerId", required = false) String containerId,
			@PathVariable(value = "parentId", required = false) String documentParentId,
			@PathVariable(value = "ownerId", required = true) String ownerId,
			@PathVariable(value = "ownerType", required = true) String ownerType,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@PutMapping(value = "/notaries/attachments/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> updateAttachmentTypeFromLotItem(
			@PathVariable("id") String lotItemId,
			@RequestBody AttachmentTypeRequest body,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Cart
	@RequestMapping(method = RequestMethod.POST, value = "/api/carts", consumes = "application/json")
	ResponseEntity<List<CartItem>> sendCartItems(
			@RequestBody CartConfirmRequest lotItemIds,
			@RequestHeader("X-Company-Token") String notaryToken);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/recipients/{owner_type}/{owner_id}")
	ResponseEntity<List<User>> getRecipientsById(
			@PathVariable("owner_type") String ownerType,
			@PathVariable("owner_id") String ownerId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Containers
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/containers/{containerId}")
	ResponseEntity<Container> getContainerById(
			@PathVariable("containerId") String containerId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/containers")
	ResponseEntity<Container> createOrGetContainer(
			@RequestBody Container container,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Documents
	@PostMapping(value = "/notaries/documents")
	ResponseEntity<DocumentRequest> linkLocationToDocument(
			@RequestBody DocumentRequest documentRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}")
	ResponseEntity<Document> getDocumentById(
			@PathVariable("id") String documentId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/download")
	ResponseEntity<Resource> getDocumentContentById(
			@PathVariable("id") String documentId,
			@RequestParam(value = "version", required = false) Integer version,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/documents/{id}/{version}/storage")
	ResponseEntity<Storage> getStorageByDocumentIdAndVersion(
			@PathVariable("id") String documentId,
			@PathVariable("version") Integer version,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Lots
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}")
	ResponseEntity<Lot> getLotById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/lots")
	Lot createLot(
			@RequestBody LotItemNotaryRequest lotItemNotaryRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.PUT, value = "/notaries/lots/{id}")
	Lot updateLot(
			@PathVariable("id") String lotId,
			@RequestBody LotItemNotaryRequest lotItemNotaryRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);


	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}/json_data")
	ResponseEntity<String> getLotJsonById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lots/{id}/json_data")
	ResponseEntity<Object> getLotJsonObjectById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/lots/{id}/json_data")
	@Headers("Content-Type: application/json")
	ResponseEntity<Object> setLotJsonObjectById(
			@PathVariable("id") String lotId,
			@RequestBody Object form,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//LotItens
	//TODO: Ver momento futuro para fazer o GET /notaries/lot_items/download
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/{id}")
	ResponseEntity<LotItem> getLotItemById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken
	);
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/{id}")
	ResponseEntity<LotItem> getLotItemById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken,
			@RequestParam(value = "nestedAttachments", defaultValue = "true", required = false) Boolean nestedAttachments
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/sha256/{sha256}")
	ResponseEntity<List<LotItem>> getLotItemBySha256(
			@PathVariable("sha256") String sha256,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/lot_items/{id}/events")
	ResponseEntity<List<Event>> getLotItemEventsById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken
	);


	@PostMapping(value = "/notaries/lot_items/{id}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Attachment> uploadFileToLotItem(
			@PathVariable("id") String lotItemId,
			@RequestPart(value = "file", required = true) MultipartFile file,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@PostMapping(value = "/notaries/lot_items/forward", produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<LotItem> forwardNewLotItem(
			@RequestBody ForwardRequest forwardRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.PUT, value = "/notaries/lot_items/{id}/protocol", consumes = "application/json")
	ResponseEntity<String> setProtocolOnLotItemById(
			@PathVariable("id") String lotId,
			@RequestHeader("X-Company-Token") String notaryToken,
			@RequestBody(required = true) String protocol
	);

	//Events
	@RequestMapping(method = RequestMethod.POST, value = "/notaries/events")
	ResponseEntity<Event> postNewEvent(
			Event evento,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Prices
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/prices/{id}")
	ResponseEntity<List<Price>> getPriceById(
			@PathVariable("id") String lotItemId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/prices/{id}/simulate")
	ResponseEntity<PriceSimulation> getPriceSimulationById(
			@RequestBody List<String> body,
			@PathVariable("id") String lotItemId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.PUT, value = "/notaries/prices/{id}")
	ResponseEntity<String> setPriceById(
			@PathVariable("id") String lotItemId,
			@RequestBody List<Price> prices,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Services
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/services/{serviceId}")
	ResponseEntity<Service> getServiceById(
			@PathVariable("serviceId") Integer serviceId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/{notaryId}/services")
	ResponseEntity<List<Service>> getServicesByNotaryId(
			@PathVariable("notaryId") Integer serviceIdId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Owners
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/{type}/{id}")
	ResponseEntity<Company> getOwner(
			@PathVariable("type") String ownerType,
			@PathVariable("id") String ownerId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/user/{id}")
	ResponseEntity<User> getUser(
			@PathVariable("id") String userId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/owners/company/{id}")
	ResponseEntity<Company> getCompany(
			@PathVariable("id") String companyId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/options")
	ResponseEntity<List<Option>> getOptions(
			@RequestParam(value = "ownerType", required = false) String ownerType,
			@RequestParam(value = "ownerId", required = false) Long ownerId,
			@RequestParam(value = "moduleName", required = false) String moduleName,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	//Wallets
	@RequestMapping(method = RequestMethod.GET, value = "/notaries/wallets/{ownerType}/{ownerId}")
	ResponseEntity<Wallet> getBalanceWallet(
			@PathVariable("ownerType") String ownerType,
			@PathVariable("ownerId") String ownerId,
			@RequestParam(name = "containerId", required = false) String containerId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.GET, value = "/notaries/wallets/{companyToken}")
	ResponseEntity<Wallet> getBalanceWallet(
			@PathVariable("companyToken") String companyToken,
			@RequestParam(name = "containerId", required = false) String containerId,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets")
	ResponseEntity<Wallet> createContainerWallet(
			@RequestBody WalletCreateRequest walletCreateRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets/credit")
	ResponseEntity<Wallet> changeLimitWallet(
			@RequestBody WalletLimitRequest walletRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);

	@RequestMapping(method = RequestMethod.POST, value = "/notaries/wallets/balance")
	ResponseEntity<Wallet> addBalanceLotItemWallet(
			@RequestBody WalletBalanceRequest walletRequest,
			@RequestHeader("X-Company-Token") String notaryToken
	);
}
