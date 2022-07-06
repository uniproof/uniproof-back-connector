package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.client.UniproofLargeFilesNotaryClient;
import br.com.uniproof.integration.api.client.UniproofNotaryClient;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import feign.FeignException;
import feign.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
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
import java.util.UUID;
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

	public List<User> getRecipientsById(String ownerType, String ownerId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getRecipientsById(ownerType, ownerId, notaryToken).getBody();
	}

	public void deleteAttachmentFromLotItem(String lotItemId, String attachmentId, @NonNull String notaryToken) {
		uniproofNotaryClient.deleteAttachmentFromLotItem(lotItemId, attachmentId, notaryToken);
	}

	private Path responseToPath(Response response) {
		if (response.status() == 200) {
			try {
				String contentDisposition = response.headers().get("content-disposition").toArray()[0].toString();
				Pattern pattern = Pattern.compile("\"(.*?)\"");
				Matcher matcher = pattern.matcher(contentDisposition);

				Path destFile = null;
				if (matcher.find()) {
					destFile = Files.createTempFile("tmp", "-" + matcher.group(1));
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

	private Path responseToPath(ResponseEntity<Resource> response) {
		if (response.getStatusCodeValue() == 200) {
			try {
				String contentDisposition = response.getHeaders().get("content-disposition").toArray()[0].toString();
				Pattern pattern = Pattern.compile("\"(.*?)\"");
				Matcher matcher = pattern.matcher(contentDisposition);

				Path destFile = null;
				if (matcher.find()) {
					destFile = Files.createTempFile("tmp", "-" + matcher.group(1));
				} else {
					destFile = Files.createTempFile("tmp", "-nonamefile.pdf");
				}
				FileUtils.copyInputStreamToFile(response.getBody().getInputStream(), destFile.toFile());
				return destFile;
			} catch (IOException ioException) {
				log.error("falha", ioException);
			}
		}
		throw new RuntimeException("Erro ao buscar arquivo na plataforma");
	}

	private Path responseToPath(MultipartFile response) {
		try {
			MultipartFile mf = response;
			Path destFile = Files.createTempFile("tmp", "-" + mf.getName());
			FileUtils.copyInputStreamToFile(mf.getInputStream(), destFile.toFile());
			return destFile;
		} catch (IOException ioException) {
			log.error("falha", ioException);
		}
		throw new RuntimeException("Erro ao buscar arquivo na plataforma");
	}

	public Path getDocumentContentById(String documentId, @NonNull String notaryToken) {
		ResponseEntity<Resource> response = uniproofNotaryClient.getDocumentContentById(documentId, null, notaryToken);
		return responseToPath(response);
	}

	public Path getDocumentVersionContentById(String documentId, Integer version, @NonNull String notaryToken) {
		ResponseEntity<Resource> response = uniproofNotaryClient.getDocumentContentById(documentId, version, notaryToken);
		return responseToPath(response);
	}

	public Path getDocumentOriginalContentById(String documentId, @NonNull String notaryToken) {
		ResponseEntity<Resource> response = uniproofNotaryClient.getDocumentContentById(documentId, 1, notaryToken);
		return responseToPath(response);
	}

	public Lot getLotById(String lotId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotById(lotId, notaryToken).getBody();
	}

	public String getLotJsonById(String lotId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotJsonById(lotId, notaryToken).getBody();
	}

	public Object getLotJsonObjectById(String lotId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotJsonObjectById(lotId, notaryToken).getBody();
	}

	public Object setLotJsonObjectById(String lotId, Object object, @NonNull String notaryToken) {
		return uniproofNotaryClient.setLotJsonObjectById(lotId, object, notaryToken);
	}


	public LotItem getLotItemById(String lotItemId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotItemById(lotItemId, notaryToken).getBody();
	}

	public List<LotItem> getLotItemBySha256(String sha256, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotItemBySha256(sha256, notaryToken).getBody();
	}

	public List<Event> getLotItemEventsById(String lotItemId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getLotItemEventsById(lotItemId, notaryToken).getBody();
	}

	public String setProtocolOnLotItemById(String lotId, @NonNull String notaryToken, String protocol) {
		return uniproofNotaryClient.setProtocolOnLotItemById(lotId, notaryToken, protocol).getBody();
	}

	public LotItem uploadFileToLotItem(String lotItemId, String name, @NonNull String notaryToken, Path file) {
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

	public DocumentRequest linkLocationToDocument(DocumentRequest documentRequest, @NonNull String notaryToken) {
		return uniproofNotaryClient.linkLocationToDocument(documentRequest, notaryToken).getBody();
	}

	public Attachment updateAttachmentType(String lotItemId, Integer attachmentTypeId, @NonNull String notaryToken) {
		return uniproofNotaryClient.updateAttachmentTypeFromLotItem(
				lotItemId,
				AttachmentTypeRequest.builder()
						.attachmentTypeId(attachmentTypeId)
						.build(),
				notaryToken
		).getBody();
	}

	public LotItem uploadAttachmentToLotItem(String lotItemId, String name, Integer attachmentTypeId, String parentId, @NonNull String notaryToken, Path file) {
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
					result = uniproofNotaryClient.uploadAttachmentToLotItem(
									lotItemId,
									mpfile,
									attachmentTypeId,
									parentId,
									uniproofApiConfig.getNotary())
							.getBody();
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


	public LotItem uploadAttachmentToOwner(String ownerType, String ownerId, String name, Integer attachmentTypeId, String parentId, @NonNull String notaryToken, Path file) {
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

			Container container = uniproofNotaryClient.createOrGetContainer(
					Container.builder()
							.containerTypeId(1)
							.ownerType("Company")
							.ownerId(Long.valueOf(ownerId))
							.name("Faturamento")
							.description("Pasta de faturamento")
							.parentId(parentId)
							.build(),
					uniproofApiConfig.getNotary()).getBody();

			result = uniproofNotaryClient.uploadAttachmentToOwner(
							mpfile,
							attachmentTypeId,
							container.getId(),
							parentId,
							ownerType,
							ownerId,
							notaryToken
					)
					.getBody();
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
			throw new RuntimeException("Falha ao anexar ao " + ownerType + ": " + ownerId, ex);
		}
		return result;
	}

	private ResponseEntity postNewEvent(Event evento, @NonNull String notaryToken) {
		ResponseEntity result = uniproofNotaryClient.postNewEvent(evento, notaryToken);
		return result;
	}

	public List<Price> getPriceById(String lotItemId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getPriceById(lotItemId, notaryToken).getBody();
	}

	public String setPriceById(String lotItemId, List<Price> prices, @NonNull String notaryToken) {
		return uniproofNotaryClient.setPriceById(lotItemId, prices, notaryToken).getBody();
	}

	public Document getDocumentById(String documentId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getDocumentById(documentId, notaryToken).getBody();
	}

	public Storage getStorageByDocumentIdAndVersion(String documentId, Integer version, @NonNull String notaryToken) {
		return uniproofNotaryClient.getStorageByDocumentIdAndVersion(documentId, version, notaryToken).getBody();
	}

	public Company getOwner(String ownerType, String ownerId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getOwner(ownerType, ownerId, notaryToken).getBody();
	}

	public User getUser(String userId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getUser(userId, notaryToken).getBody();
	}

	public Company getCompany(String companyId, @NonNull String notaryToken) {
		return uniproofNotaryClient.getCompany(companyId, notaryToken).getBody();
	}

	public Boolean addEvent(@NonNull String notaryToken, String lotItemId, String status, String msg, Boolean force) {
		LotItem processo = getLotItemById(lotItemId, notaryToken);

		if (force || !processo.getEvent().getStatus().equalsIgnoreCase(status)) {

			Event event = Event.builder()
					.status(status)
					.ownerType("LotItem")
					.lotItemId(lotItemId)
					.description(msg)
					.build();

			ResponseEntity postexigencia2 = postNewEvent(event, notaryToken);
			if (postexigencia2.getStatusCodeValue() >= 300) {
				throw new RuntimeException("Erro movendo status " + status + " - lot_item_id: " + lotItemId + " motivo: " + postexigencia2.getBody());
			}
			return postexigencia2.getStatusCodeValue() < 300;
		}
		return false;
	}

	public void addLotEvent(@NonNull String notaryToken, String lotItemId, String status, String msg) {

		Event event = Event.builder()
				.status(status)
				.ownerType("Lot")
				.lotItemId(lotItemId)
				.description(msg)
				.build();

		ResponseEntity postexigencia2 = postNewEvent(event, notaryToken);

	}

	public List<Option> getOptions(
			String ownerType,
			Long ownerId,
			String moduleName,
			@NonNull String notaryToken
	) {
		return uniproofNotaryClient.getOptions(ownerType, ownerId, moduleName, notaryToken).getBody();
	}


	public Wallet createContainerWallet(
			String containerId,
			BigDecimal value,
			String observation,
			@NonNull String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.containerId(containerId)
				.credit(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.createContainerWallet(walletRequest, notaryToken).getBody();
	}

	public Wallet changeLimitWallet(
			String lotItemId,
			BigDecimal value,
			String observation,
			@NonNull String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.lotItemId(lotItemId)
				.credit(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.changeLimitWallet(walletRequest, notaryToken).getBody();
	}

	public Wallet addBalanceLotItemWallet(
			String lotItemId,
			BigDecimal value,
			String observation,
			@NonNull String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.lotItemId(lotItemId)
				.balance(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.addBalanceLotItemWallet(walletRequest, notaryToken).getBody();
	}

	public Wallet addBalanceCompanyWallet(
			String companyToken,
			BigDecimal value,
			String observation,
			@NonNull String notaryToken) {
		Wallet wallet = uniproofNotaryClient.getBalanceWallet(companyToken, null, uniproofApiConfig.getNotary()).getBody();
		WalletRequest walletRequest = WalletRequest.builder()
				.ownerType(wallet.getOwnerType())
				.ownerId(wallet.getOwnerId())
				.balance(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.addBalanceLotItemWallet(walletRequest, notaryToken).getBody();
	}

	public Wallet addBalanceContainerWallet(
			String containerId,
			BigDecimal value,
			String observation,
			@NonNull String notaryToken) {
		WalletRequest walletRequest = WalletRequest.builder()
				.containerId(containerId)
				.balance(value)
				.observation(observation)
				.build();
		return uniproofNotaryClient.addBalanceLotItemWallet(walletRequest, notaryToken).getBody();
	}

	public Wallet getBalanceWallet(
			@NonNull String ownerType,
			@NonNull String ownerId,
			String containerId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getBalanceWallet(ownerType, ownerId, containerId, notaryToken).getBody();
	}

	public Wallet getBalanceWallet(
			@NonNull String companyToken,
			String containerId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getBalanceWallet(companyToken, containerId, notaryToken).getBody();
	}

	public br.com.uniproof.integration.api.beans.Service getServiceById(
			@NonNull Integer serviceId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getServiceById(
				serviceId,
				notaryToken).getBody();
	}

	public List<br.com.uniproof.integration.api.beans.Service> getServicesByNotaryId(
			Integer serviceIdId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getServicesByNotaryId(
				serviceIdId,
				notaryToken
		).getBody();
	}

	public LotItem forwardNewLotItem(
			ForwardRequest forwardRequest,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.forwardNewLotItem(
				forwardRequest,
				notaryToken
		).getBody();
	}

	public Container getContainerById(
			String containerId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getContainerById(
				containerId,
				notaryToken
		).getBody();
	}

	public Container createOrGetContainer(
			Container container,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.createOrGetContainer(
				container,
				notaryToken
		).getBody();
	}

	public List<CartItem> sendCartItems(
			CartConfirmRequest lotItemIds,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.sendCartItems(
				lotItemIds,
				notaryToken).getBody();
	}


	public List<Attachment> getAttachmentFromLotItem(
			String lotItemId,
			@NonNull String notaryToken) {
		return uniproofNotaryClient.getAttachmentFromLotItem(
				lotItemId,
				notaryToken
		).getBody();
	}
}
