package br.com.uniproof.integration.api.client;

import br.com.uniproof.integration.api.beans.Attachment;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.regex.Pattern;

@Service
@Slf4j
public class UniproofLargeFilesNotaryClient {

    @Autowired
    private UniproofApiCoreService uniproofApiCoreService;

    @Autowired
    private UniproofApiConfig uniproofApiConfig;

    public Attachment uploadFileToLotItem(String lotItemId, String name, Integer attachmentId, String notaryToken, Path file) {
        Attachment result = null;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("Authorization", "Bearer " + uniproofApiCoreService.getToken(uniproofApiConfig.getLoginEmail(), uniproofApiConfig.getLoginPass()));
            headers.add("X-Company-Token", notaryToken);
            headers.add("Accept", "application/json;charset=UTF-8");

            String nfdNormalizedString = Normalizer.normalize(name, Normalizer.Form.NFD);
            Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
            name = pattern.matcher(nfdNormalizedString).replaceAll("");
            name = name.replaceAll("[^a-zA-Z0-9\\._]+", "_");
            Path novoNome = Paths.get(file.toFile().getParent() + "/" + name);
            Files.deleteIfExists(novoNome);
            Files.move(file, novoNome);
            FileSystemResource fileSystemResource = new FileSystemResource(novoNome.toFile());

            MultiValueMap<String, Object> body
                    = new LinkedMultiValueMap<>();
            body.add("file", fileSystemResource);
            body.add("attachmentTypeId", attachmentId);
            HttpEntity<MultiValueMap<String, Object>> requestEntity
                    = new HttpEntity<>(body, headers);

            String serverUrl = uniproofApiConfig.getRestUrl() + "/notaries/lot_items/" + lotItemId + "/attachments";
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Attachment> response = restTemplate
                    .postForEntity(serverUrl, requestEntity, Attachment.class);
            Files.deleteIfExists(novoNome);
            return response.getBody();

        } catch (Exception ex) {
            log.error("Falha ao criar arquivo na plataforma: ", ex);
        }
        return null;
    }
}
