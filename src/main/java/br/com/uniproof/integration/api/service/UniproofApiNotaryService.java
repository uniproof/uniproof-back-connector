package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.client.UniproofLargeFilesNotaryClient;
import br.com.uniproof.integration.api.client.UniproofNotaryClient;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import feign.FeignException;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UniproofApiNotaryService {

	@Autowired
	private UniproofNotaryClient uniproofNotaryClient;

	@Autowired
	private UniproofLargeFilesNotaryClient uniproofLargeFilesNotaryClient;

	@Autowired
	private UniproofApiConfig uniproofApiConfig;

	public List<User> getRecipientsById(String ownerType, String ownerId, String notaryToken) {
		return uniproofNotaryClient.getRecipientsById(ownerType, ownerId, notaryToken);
	}

	public void deleteAttachmentFromLotItem(String lotItemId, String attachmentId, String notaryToken) {
		uniproofNotaryClient.deleteAttachmentFromLotItem(lotItemId, attachmentId, notaryToken);
	}

	private Path responseToPath(Response response) {
		if (response.status() == 200 ) {
			try {
				//response.headers();
				String contentDisposition = response.headers().get("content-disposition").toArray()[0].toString();
				Pattern pattern = Pattern.compile("\"(.*?)\"");
				Matcher matcher = pattern.matcher(contentDisposition);

				Path destFile = null;
				if (matcher.find()) {
					destFile = Files.createTempFile("tmp", "-"+matcher.group(1));
				} else {
					destFile = Files.createTempFile("tmp", "-nonamefile.pdf");
				}
				FileUtils.copyInputStreamToFile(response.body().asInputStream(), destFile.toFile());
				return destFile;
			} catch (IOException ioException) {
				log.error("falha", ioException);
			}
		}
		throw new RuntimeException("Erro ao buscar arquivo na plataforma");
	}

	public Path getDocumentContentById(String documentId, String notaryToken) {
		Response response = uniproofNotaryClient.getDocumentContentById(documentId, notaryToken);
		return responseToPath(response);
	}

	public Path getDocumentVersionContentById(String documentId, Integer version, String notaryToken) {
		Response response = uniproofNotaryClient.getDocumentVersionContentById(documentId, version, notaryToken);
		return responseToPath(response);
	}

	public Path getDocumentOriginalContentById(String documentId, String notaryToken) {
		Response response = uniproofNotaryClient.getDocumentOriginalContentById(documentId, notaryToken);
		return responseToPath(response);
	}

	public Lot getLotById(String lotId, String notaryToken) {
		return uniproofNotaryClient.getLotById(lotId, notaryToken);
	}

	public String getLotJsonById(String lotId, String notaryToken) {
		return uniproofNotaryClient.getLotJsonById(lotId, notaryToken);
	}

	public Object getLotJsonObjectById(String lotId, String notaryToken) {
		return uniproofNotaryClient.getLotJsonObjectById(lotId, notaryToken);
	}

	public LotItem getLotItemById(String lotItemId, String companyToken) {
		return uniproofNotaryClient.getLotItemById(lotItemId, companyToken);
	}

	public List<LotItem> getLotItemBySha256(String sha256, String companyToken) {
		return uniproofNotaryClient.getLotItemBySha256(sha256, companyToken);
	}

	public List<Event> getLotItemEventsById(String lotItemId, String companyToken) {
		return uniproofNotaryClient.getLotItemEventsById(lotItemId, companyToken);
	}

	public String setProtocolOnLotItemById(String lotId, String companyToken, String protocol) {
		return uniproofNotaryClient.setProtocolOnLotItemById(lotId, companyToken, protocol);
	}

	public LotItem uploadFileToLotItem(String lotItemId, String name, String notaryToken, Path file) {
		LotItem result = null;

		try {
			if (Files.size(file) > 40 * 1024 * 1024) {
				result = uniproofLargeFilesNotaryClient.uploadFileToLotItem(lotItemId, name, 4, notaryToken, file);
			} else {
				try (InputStream is = Files.newInputStream(file)) {
					MultipartFile mpfile = new MockMultipartFile(
							"file",
							name,
							Files.probeContentType(file),
							is);
					result = uniproofNotaryClient.uploadFileToLotItem(lotItemId, mpfile, uniproofApiConfig.getNotary()).getBody();
				} catch (Exception ex) {
					if (ex instanceof FeignException) {
						FeignException feignException = (FeignException) ex;
						if (feignException.responseBody().isPresent()) {
							Charset charset = Charset.defaultCharset();
							String response = charset.decode(feignException.responseBody().get()).toString();
							log.error("Falha ao criar arquivo na plataforma: " + response);
						}
					} else {
						log.error("Falha ao criar arquivo na plataforma: ", ex);
					}
					throw new RuntimeException("Falha ao anexar ao lotItem " + lotItemId, ex);
				}
			}
		} catch (Exception ex) {
			log.error("Falha ao criar arquivo na plataforma: ", ex);
		}
		return result;
	}

	public DocumentRequest linkLocationToDocument(DocumentRequest documentRequest, String notaryToken) {
		return uniproofNotaryClient.linkLocationToDocument(documentRequest, notaryToken).getBody();
	}

	public Attachment updateAttachmentType(String lotItemId, Integer attachmentTypeId, String notaryToken) {
		return uniproofNotaryClient.updateAttachmentType(
				lotItemId,
				AttachmentTypeRequest.builder()
						.attachmentTypeId(attachmentTypeId)
						.build(),
				notaryToken
		).getBody();
	}

	public LotItem uploadAttachmentToLotItem(String lotItemId, String name, Integer attachmentTypeId, String notaryToken, Path file) {
		LotItem result = null;
		if (file == null) {
			return null;
		}
		try {
			if (Files.size(file) > 40 * 1024 * 1024) {
				result = uniproofLargeFilesNotaryClient.uploadFileToLotItem(lotItemId, name, attachmentTypeId, notaryToken, file);
			} else {
				try (InputStream is = Files.newInputStream(file)) {
					MultipartFile mpfile = new MockMultipartFile(
							"file",
							name,
							Files.probeContentType(file),
							is);
					result = uniproofNotaryClient.uploadAttachmentToLotItem(lotItemId, mpfile, attachmentTypeId, uniproofApiConfig.getNotary()).getBody();
				} catch (Exception ex) {
					if (ex instanceof FeignException) {
						FeignException feignException = (FeignException) ex;
						if (feignException.responseBody().isPresent()) {
							Charset charset = Charset.defaultCharset();
							String response = charset.decode(feignException.responseBody().get()).toString();
							log.error("Falha ao criar arquivo na plataforma: " + response);
						}
					} else {
						log.error("Falha ao criar arquivo na plataforma: ", ex);
					}
					throw new RuntimeException("Falha ao anexar ao lotItem " + lotItemId, ex);
				}
			}
		} catch (Exception ex) {
			log.error("Falha ao criar arquivo na plataforma: ", ex);
		}
		return result;
	}

	public LotItem uploadAttachmentParentToLotItem(
			String lotItemId,
			String name,
			Path file,
			Integer attachmentTypeId,
			String parentId,
			String companyToken
	) {
		LotItem result = null;
		if (file == null) {
			return null;
		}
		try (InputStream is = Files.newInputStream(file)) {
			MultipartFile mpfile = new MockMultipartFile(
					"file",
					name,
					Files.probeContentType(file),
					is);
			result = uniproofNotaryClient.uploadAttachmentParentToLotItem(lotItemId, mpfile, attachmentTypeId, parentId, companyToken).getBody();
		} catch (Exception ex) {
			if (ex instanceof FeignException) {
				FeignException feignException = (FeignException) ex;
				if (feignException.responseBody().isPresent()) {
					Charset charset = Charset.defaultCharset();
					String response = charset.decode(feignException.responseBody().get()).toString();
					log.error("Falha ao criar arquivo na plataforma: " + response);
				}
			} else {
				log.error("Falha ao criar arquivo na plataforma: ", ex);
			}
			throw new RuntimeException("Falha ao anexar ao lotItem " + lotItemId, ex);
		}
		return result;
	}

	private ResponseEntity postNewEvent(Event evento, String companyToken) {
		ResponseEntity result = uniproofNotaryClient.postNewEvent(evento, companyToken);
		for (int i = 0; i < 5; i++) {
			if (result.getStatusCodeValue() != 429) {
				break;
			}
			result = uniproofNotaryClient.postNewEvent(evento, companyToken);
		}
		return result;
	}

	public List<Price> getPriceById(String lotItemId, String notaryToken) {
		return uniproofNotaryClient.getPriceById(lotItemId, notaryToken);
	}

	public Response setPriceById(String lotItemId, List<Price> prices, String notaryToken) {
		return uniproofNotaryClient.setPriceById(lotItemId, prices, notaryToken);
	}

	public Document getDocumentById(String documentId, String notaryToken) {
		return uniproofNotaryClient.getDocumentById(documentId, notaryToken);
	}

	public Storage getStorageByDocumentIdAndVersion(String documentId, Integer version, String notaryToken) {
		return uniproofNotaryClient.getStorageByDocumentIdAndVersion(documentId, version, notaryToken);
	}

	public Company getOwner(String ownerType, String ownerId, String notaryToken) {
		return uniproofNotaryClient.getOwner(ownerType, ownerId, notaryToken);
	}

	public User getUser(String userId, String notaryToken) {
		return uniproofNotaryClient.getUser(userId, notaryToken);
	}

	public Company getCompany(String companyId, String notaryToken) {
		return uniproofNotaryClient.getCompany(companyId, notaryToken);
	}

	public Boolean addEvent(String uniproofNotaryToken, String lotItemId, String status, String msg, Boolean force) {
		LotItem processo = getLotItemById(lotItemId, uniproofNotaryToken);

		if (force || !processo.getEvent().getStatus().equalsIgnoreCase(status)) {

			Event event = Event.builder()
					.status(status)
					.ownerType("LotItem")
					.lotItemId(lotItemId)
					.description(msg)
					.build();

			ResponseEntity postexigencia2 = postNewEvent(event, uniproofNotaryToken);
			if (postexigencia2.getStatusCodeValue() >= 300) {
				throw new RuntimeException("Erro movendo status " + status + " - lot_item_id: " + lotItemId + " motivo: " + postexigencia2.getBody());
			}
			return postexigencia2.getStatusCodeValue() < 300;
		}
		return false;
	}

	public void addLotEvent(String uniproofNotaryToken, String lotItemId, String status, String msg) {

		Event event = Event.builder()
				.status(status)
				.ownerType("Lot")
				.lotItemId(lotItemId)
				.description(msg)
				.build();

		ResponseEntity postexigencia2 = postNewEvent(event, uniproofNotaryToken);

	}

	public List<Option> getOptions(
			String ownerType,
			Long ownerId,
			String moduleName,
			String notaryToken
	) {
		return uniproofNotaryClient.getOptions(ownerType, ownerId, moduleName, notaryToken);
	}


	public Wallet createContainerWallet(
			String containerId,
			BigDecimal value,
			String observation,
			String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.containerId(containerId)
				.credit(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.createContainerWallet(walletRequest, notaryToken);
	}

	public Wallet changeLimitWallet(
			String lotItemId,
			BigDecimal value,
			String observation,
			String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.lotItemId(lotItemId)
				.credit(value)
				.observation(observation)
				.build();
		;
		return uniproofNotaryClient.changeLimitWallet(walletRequest, notaryToken);
	}

	public Wallet addBalanceLotItemWallet(
			String lotItemId,
			BigDecimal value,
			String observation,
			String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.lotItemId(lotItemId)
				.balance(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.addBalanceLotItemWallet(walletRequest, notaryToken);
	}
}
