package br.com.uniproof.integration.api.service;

import br.com.uniproof.integration.api.beans.*;
import br.com.uniproof.integration.api.beans.options.ServicesAttachmentsOptions;
import br.com.uniproof.integration.api.beans.options.ServicesAttachmentsRule;
import br.com.uniproof.integration.api.client.UniproofLargeFilesNotaryClient;
import br.com.uniproof.integration.api.client.UniproofNotaryClient;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import feign.FeignException;
import feign.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UniproofApiNotaryService {

    @Autowired
    private UniproofNotaryClient uniproofNotaryClient;

    @Autowired
    private UniproofLargeFilesNotaryClient uniproofLargeFilesNotaryClient;

    @Autowired
    private UniproofApiConfig uniproofApiConfig;


    public List<ServicesAttachmentsOptions> findProcessServicesBehavior(
            String ownerType,
            Long ownerId,
            String moduleName,
            @NonNull String notaryToken
    ) {
        List<ServicesAttachmentsOptions> result = new ArrayList<>();
        List<Option> optionList = getOptions(ownerType, ownerId, moduleName, notaryToken);
        optionList.forEach(opt -> {
            try {
                ServicesAttachmentsOptions servicesAttachmentsOptions = opt.getValueAsObject(ServicesAttachmentsOptions.class);
                Collections.sort(
                        servicesAttachmentsOptions.getServicesAttachmentsRules(),
                        Comparator.comparing(ServicesAttachmentsRule::getWeight));
                Collections.reverse(servicesAttachmentsOptions.getServicesAttachmentsRules());
                result.add(servicesAttachmentsOptions);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });


        return result;
    }

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
                FileOutputStream fos = new FileOutputStream(destFile.toFile());
                FileCopyUtils.copy(response.body().asInputStream(), fos);
                return destFile;
            } catch (IOException ioException) {
                log.error("falha", ioException);
            }
        }
        throw new RuntimeException("Erro ao buscar arquivo na plataforma");
    }

    private Path responseToPath(ResponseEntity<Resource> response) {
        if (response.getStatusCode() == HttpStatus.OK) {
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
                FileOutputStream fos = new FileOutputStream(destFile.toFile());
                FileCopyUtils.copy(response.getBody().getInputStream(), fos);

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
            FileOutputStream fos = new FileOutputStream(destFile.toFile());
            FileCopyUtils.copy(mf.getInputStream(), fos);
            return destFile;
        } catch (IOException ioException) {
            log.error("falha", ioException);
        }
        throw new RuntimeException("Erro ao buscar arquivo na plataforma");
    }

    public Path getDocumentContentById(String documentId, @NonNull String notaryToken) {
        Response response = uniproofNotaryClient.getDocumentContentById(documentId, null, notaryToken);
        return responseToPath(response);
    }

    public Path getDocumentVersionContentById(String documentId, Integer version, @NonNull String notaryToken) {
        Response response = uniproofNotaryClient.getDocumentContentById(documentId, version, notaryToken);
        return responseToPath(response);
    }

    public Path getDocumentOriginalContentById(String documentId, @NonNull String notaryToken) {
        Response response = uniproofNotaryClient.getDocumentContentById(documentId, 1, notaryToken);
        return responseToPath(response);
    }

    public Lot getLotById(String lotId, @NonNull String notaryToken) {
        return uniproofNotaryClient.getLotById(lotId, notaryToken).getBody();
    }

    public Lot createLot(LotItemNotaryRequest lotItemNotaryRequest, String notaryToken) {
        return uniproofNotaryClient.createLot(lotItemNotaryRequest, notaryToken);
    }

    public Lot updateLot(String lotId, LotItemNotaryRequest lotItemNotaryRequest, String notaryToken) {
        return uniproofNotaryClient.updateLot(lotId, lotItemNotaryRequest, notaryToken);
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

    public List<Company> getCompaniesByRoleOrId(String roleOrId, String notaryToken) {
        return uniproofNotaryClient.getCompaniesByRoleOrId(roleOrId, notaryToken).getBody();
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

    public Attachment uploadRegisteredFileToLotItem(String lotItemId, String name, @NonNull String notaryToken, Path file) {
        Integer registered_document_att_type = 5;
        return uploadAttachmentToLotItem(lotItemId, name, registered_document_att_type, null, notaryToken, file);
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

    public List<Attachment> convertFlatAttachmentListToNestedList(List<Attachment> attachmentList) {
        Map<String, Attachment> result = new HashMap<>();
        Set<Attachment> resultList = new HashSet<>();
        List<Attachment> pending = new LinkedList<>(attachmentList);
        int i = 0;
        while (!pending.isEmpty() && i < 10) {
            i++;
            for (Attachment att : attachmentList) {
                if (ObjectUtils.isEmpty(att.getParentId()) && att.getRemovedAt() == null) {
                    result.putIfAbsent(att.getDocument().getId(), att);
                    resultList.add(att);
                    pending.remove(att);
                } else {
                    if (!ObjectUtils.isEmpty(result.get(att.getParentId()))) {
                        Attachment parent = result.get(att.getParentId());
                        result.putIfAbsent(att.getDocument().getId(), att);
                        parent.getChildren().add(att);
                        att.setParent(parent);
                        pending.remove(att);
                    }
                }
            }
        }
        return new ArrayList<>(resultList);
    }

    public Attachment uploadAttachmentToLotItem(String lotItemId, String name, Integer attachmentTypeId, String parentId, @NonNull String notaryToken, Path file) {
        Attachment result = null;
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


    public Attachment uploadAttachmentToOwner(
            String ownerType,
            String ownerId,
            String name,
            Integer attachmentTypeId,
            String documentParentId,
            String containerId,
            @NonNull String notaryToken,
            Path file) {
        Attachment result = null;
        if (file == null) {
            return null;
        }

        try (InputStream is = Files.newInputStream(file)) {
            MultipartFile mpfile = new MockMultipartFile(
                    "file",
                    name,
                    Files.probeContentType(file),
                    is);

            result = uniproofNotaryClient.uploadAttachmentToOwner(
                            mpfile,
                            attachmentTypeId,
                            containerId,
                            documentParentId,
                            ownerId,
                            ownerType,
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
        } finally {
            try {
                Files.deleteIfExists(file);
            } catch (IOException ignored) {
            }
        }
        return result;
    }

    public Attachment importAttachmentsFromParent(
            String lotItemId,
            String attachmentId,
            Integer attachmentTypeId,
            String parentId,
            @NonNull String notaryToken) {
        Attachment result = null;
        try {
            result = uniproofNotaryClient.importAttachmentsFromParent(
                            AttachmentTypeRequest.builder()
                                    .attachmentId(attachmentId)
                                    .attachmentTypeId(attachmentTypeId)
                                    .parentId(parentId)
                                    .lotItemId(lotItemId)
                                    .build()
                            , notaryToken)
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
            throw new RuntimeException("Falha ao anexar ao " + lotItemId + ": " + attachmentId, ex);
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

    public PriceSimulation getPriceSimulationById(
            String lotItemId,
            String notaryToken) {
        return uniproofNotaryClient.getPriceSimulationById(
                new ArrayList<>(),
                lotItemId,
                notaryToken).getBody();
    }

    public String setPriceById(String lotItemId, List<Price> prices, @NonNull String notaryToken) {
        prices = prices.stream().filter(x -> x.getValue() != null).collect(Collectors.toList());
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
        return addEvent(notaryToken, lotItemId, status, msg, force, false);
    }

    public Boolean addEvent(@NonNull String notaryToken, String lotItemId, String status, String msg, Boolean force, Boolean skipBalanceValidation) {
        LotItem processo = getLotItemById(lotItemId, notaryToken);

        if (force || !processo.getEvent().getStatus().equalsIgnoreCase(status)) {

            Event event = Event.builder()
                    .status(status)
                    .ownerType("LotItem")
                    .lotItemId(lotItemId)
                    .description(msg)
                    .skipBalanceValidation(skipBalanceValidation)
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
            @NonNull String notaryToken) {
        WalletCreateRequest walletCreateRequest = WalletCreateRequest.builder()
                .containerId(containerId)
                .credit(value)
                .build();
        return uniproofNotaryClient.createContainerWallet(walletCreateRequest, notaryToken).getBody();
    }


    public Wallet changeLimitWallet(
            String lotItemId,
            BigDecimal value,
            String observation,
            @NonNull String notaryToken) {
        WalletLimitRequest walletRequest = WalletLimitRequest.builder()
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
        WalletBalanceRequest walletRequest = WalletBalanceRequest.builder()
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
        WalletBalanceRequest walletRequest = WalletBalanceRequest.builder()
                .companyId(companyToken)
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

        Container container = uniproofNotaryClient.getContainerById(containerId, uniproofApiConfig.getNotary()).getBody();
        Wallet wallet = getBalanceWallet(container.getOwnerType(), String.valueOf(container.getOwnerId()), null, container, uniproofApiConfig.getNotary());
        WalletBalanceRequest walletRequest = WalletBalanceRequest.builder()
                .containerId(containerId)
                .balance(value)
                .observation(observation)
                .build();
        return uniproofNotaryClient.addBalanceLotItemWallet(walletRequest, notaryToken).getBody();
    }

    public Wallet getBalanceWallet(
            String ownerType,
            String ownerId,
            String companyToken,
            Container container,
            @NonNull String notaryToken) {

        Wallet baseWallet = null;
        if (ObjectUtils.isEmpty(companyToken)) {
            baseWallet = uniproofNotaryClient.getBalanceWallet(ownerType, ownerId, container.getId(), notaryToken).getBody();
        } else {
            baseWallet = uniproofNotaryClient.getBalanceWallet(
                            companyToken,
                            ObjectUtils.isEmpty(container) ? null : container.getId(),
                            notaryToken)
                    .getBody();
        }
        Wallet wallet = baseWallet;
		/*
		if (container != null
				&& baseWallet.getOwnerType().equalsIgnoreCase("Company")
				&& !ObjectUtils.isEmpty(container.getDescription())) {
			String desc = container.getDescription().replaceAll("\\D", "");
			/if (CpfCnpjValidator.isValid(desc)) {
				wallet = uniproofNotaryClient.createContainerWallet(
						WalletCreateRequest.builder()
								.containerId(container.getId())
								.credit(baseWallet.getCredit())
								.build(),
						uniproofApiConfig.getNotary()
				).getBody();
			}
		}
		 */
        return wallet;
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

    // =========================================================================
    // Operacoes acrescentadas na cobertura completa do swagger /notaries/doc
    //
    // Estes metodos devolvem ApiResponse<T> em vez de lancar: a API sinaliza
    // falha com status HTTP de erro e corpo {statusCode, message}, e o
    // UniproofApiErrorDecoder transforma isso em UniproofApiException. Aqui a
    // excecao e capturada e entregue dentro do objeto de retorno, para o
    // chamador decidir com isSuccess()/getErrorMessage() sem try/catch.
    //
    // Os metodos anteriores a esta secao seguem devolvendo o corpo direto e
    // propagando a excecao, como sempre fizeram.
    // =========================================================================

    /** Executa a chamada e converte o erro da API no proprio retorno. */
    private <T> ApiResponse<T> executar(java.util.function.Supplier<ResponseEntity<T>> chamada) {
        try {
            ResponseEntity<T> resposta = chamada.get();
            return ApiResponse.ok(resposta.getStatusCode().value(), resposta.getBody());
        } catch (UniproofApiException e) {
            return ApiResponse.fail(e);
        }
    }

    /**
     * Anexos do processo <strong>ja aninhados pela API</strong> (pai, filho,
     * neto). Alternativa a buscar a lista plana com
     * {@link #getAttachmentFromLotItem} e montar a arvore localmente com
     * {@link #convertFlatAttachmentListToNestedList}.
     */
    public ApiResponse<List<Attachment>> getNestedAttachmentFromLotItem(
            String lotItemId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getNestedAttachmentFromLotItem(
                lotItemId,
                notaryToken
        ));
    }

    /**
     * Servico do processo, sem precisar carregar o lot_item inteiro.
     *
     * <p>O tipo vem qualificado porque esta classe importa
     * {@code org.springframework.stereotype.Service}, que sombreia o bean
     * {@code Service} trazido pelo import curinga de beans.</p>
     */
    public ApiResponse<br.com.uniproof.integration.api.beans.Service> getServiceByLotItemId(
            String lotItemId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getServiceByLotItemId(
                lotItemId,
                notaryToken
        ));
    }

    /**
     * Lista os processos da serventia. Todos os filtros sao opcionais; a API
     * pagina por {@code offset}/{@code limit} (default 25 no servidor).
     */
    public ApiResponse<List<LotItem>> getLotItems(
            String lotId,
            String status,
            String search,
            String parentId,
            Boolean archived,
            String sortColumn,
            String sortOrder,
            Integer offset,
            Integer limit,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getLotItems(
                lotId,
                status,
                search,
                parentId,
                archived,
                sortColumn,
                sortOrder,
                offset,
                limit,
                notaryToken
        ));
    }

    /**
     * Relatorio de processos da empresa. As colunas do resultado variam com os
     * filtros informados, por isso vem como mapa em {@link LotItemReport}.
     */
    public ApiResponse<LotItemReport> getLotItemsReport(
            Map<String, Object> filtros,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getLotItemsReport(
                filtros,
                notaryToken
        ));
    }

    /** Relacao dos documentos dos processos filtrados, sem baixar o ZIP. */
    public ApiResponse<List<DownloadListItem>> getLotItemsDownloadList(
            Map<String, Object> filtros,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getLotItemsDownloadList(
                filtros,
                notaryToken
        ));
    }

    /** URL do ZIP com os documentos dos processos filtrados. */
    public ApiResponse<String> getLotItemsZipLink(
            Map<String, Object> filtros,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getLotItemsZipLink(
                filtros,
                notaryToken
        ));
    }

    /** Tags disponiveis na serventia. */
    public ApiResponse<List<Tag>> getTags(
            String search,
            String sortColumn,
            String sortOrder,
            Integer offset,
            Integer limit,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getTags(
                search,
                sortColumn,
                sortOrder,
                offset,
                limit,
                notaryToken
        ));
    }

    /** Marca o processo com uma tag existente. */
    public ApiResponse<Tag> addTagToLotItem(
            String lotItemId,
            String tagId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.addTagToLotItem(
                lotItemId,
                TagRequest.builder().tagId(tagId).build(),
                notaryToken
        ));
    }

    /** Remove a tag do processo. Devolve o corpo cru da resposta ({@code message}). */
    public ApiResponse<String> removeTagFromLotItem(
            String lotItemId,
            String tagId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.removeTagFromLotItem(
                lotItemId,
                tagId,
                notaryToken
        ));
    }

    /**
     * Move o processo para outro status do workflow.
     *
     * @param skipBalanceValidation pula a validacao de saldo da carteira
     * @return corpo cru da resposta ({@code message}), como em
     *         {@link #setProtocolOnLotItemById}
     */
    public ApiResponse<String> updateWorkflowStatus(
            String lotItemId,
            String status,
            String description,
            Boolean skipBalanceValidation,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.updateWorkflowStatus(
                lotItemId,
                WorkflowStatusRequest.builder()
                        .status(status)
                        .description(description)
                        .skipBalanceValidation(skipBalanceValidation)
                        .build(),
                notaryToken
        ));
    }

    /** Troca a serventia responsavel pelo processo, registrando o evento informado. */
    public ApiResponse<String> changeLotItemNotary(
            String lotItemId,
            String notaryId,
            String comment,
            String event,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.changeLotItemNotary(
                lotItemId,
                NotaryChangeRequest.builder()
                        .notaryId(notaryId)
                        .comment(comment)
                        .event(event)
                        .build(),
                notaryToken
        ));
    }

    /** Cria o mesmo evento em varios processos de uma vez. */
    public ApiResponse<List<Event>> postNewEventBulk(
            List<String> lotItemIds,
            String status,
            String description,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.postNewEventBulk(
                EventBulkRequest.builder()
                        .lotItemIds(lotItemIds)
                        .status(status)
                        .description(description)
                        .build(),
                notaryToken
        ));
    }

    /** Envia os processos ao cartorio (fechamento de carrinho da serventia). */
    public ApiResponse<List<LotItem>> sendLotItemsToNotary(
            NotaryCartRequest cartRequest,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.sendLotItemsToNotary(
                cartRequest,
                notaryToken
        ));
    }

    /**
     * Troca o tipo de um anexo pelo <strong>nome</strong> do tipo, registrando
     * um evento com a justificativa. Complementa
     * {@link #updateAttachmentType(String, Integer, String)}, que usa o id.
     */
    public ApiResponse<Attachment> updateAttachmentTypeByName(
            String attachmentId,
            String attachmentTypeName,
            String reason,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.updateAttachmentTypeByName(
                attachmentId,
                AttachmentTypeUpdateRequest.builder()
                        .attachmentTypeName(attachmentTypeName)
                        .reason(reason)
                        .build(),
                notaryToken
        ));
    }

    /** Papeis (roles) de uma empresa. */
    public ApiResponse<List<Role>> getRolesByCompanyId(
            String companyId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getRolesByCompanyId(
                companyId,
                notaryToken
        ));
    }

    /** Usuarios visiveis para a serventia. */
    public ApiResponse<List<User>> getUsers(
            Long companyId,
            Boolean onlyActive,
            Integer offset,
            Integer limit,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getUsers(
                companyId,
                onlyActive,
                null,
                null,
                null,
                offset,
                limit,
                notaryToken
        ));
    }

    /** Um usuario pelo id. */
    public ApiResponse<User> getUserById(
            String userId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getUserById(
                userId,
                null,
                null,
                notaryToken
        ));
    }

    /** Cria ou atualiza o endereco de um owner. */
    public ApiResponse<Address> createOrUpdateAddress(
            AddressRequest addressRequest,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.createOrUpdateAddress(
                addressRequest,
                notaryToken
        ));
    }

    /** Uma opcao de configuracao especifica (por nome), em vez da lista toda. */
    public ApiResponse<Option> getOption(
            String ownerType,
            String ownerId,
            String moduleName,
            String name,
            Integer serviceId,
            @NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getOption(
                ownerType,
                ownerId,
                moduleName,
                name,
                serviceId,
                notaryToken
        ));
    }

    /** Lotes da serventia. */
    public ApiResponse<List<Lot>> getLots(@NonNull String notaryToken) {
        return executar(() -> uniproofNotaryClient.getLots(notaryToken));
    }
}
