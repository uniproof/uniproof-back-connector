package br.com.uniproof.integration.api;

import br.com.uniproof.integration.api.beans.Attachment;
import br.com.uniproof.integration.api.client.UniproofLargeFilesNotaryClient;
import br.com.uniproof.integration.api.client.UniproofNotaryClient;
import br.com.uniproof.integration.api.config.UniproofApiConfig;
import br.com.uniproof.integration.api.service.UniproofApiCoreService;
import br.com.uniproof.integration.api.service.UniproofApiNotaryService;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Production wiring ({@code UniproofClientConfig} with token interceptor,
 * rate limit interceptor, error decoder and retryer) against a local server
 * that rejects the first call to each path with 429, as the API does when the
 * rate limit is exceeded. No credentials needed.
 */
@SpringBootTest(classes = UniproofClientRateLimitWiringTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UniproofClientRateLimitWiringTest {

    @Configuration
    @EnableAutoConfiguration
    @EnableFeignClients(clients = UniproofNotaryClient.class)
    @Import({UniproofApiConfig.class, UniproofApiCoreService.class,
            UniproofApiNotaryService.class, UniproofLargeFilesNotaryClient.class})
    static class TestConfig {
    }

    private static final HttpServer SERVER;
    private static final Map<String, AtomicInteger> CHAMADAS = new ConcurrentHashMap<>();

    static {
        try {
            SERVER = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        SERVER.createContext("/", troca -> {
            String caminho = troca.getRequestURI().getPath();
            int chamada = CHAMADAS.computeIfAbsent(caminho, k -> new AtomicInteger()).incrementAndGet();
            troca.getRequestBody().readAllBytes();
            int status;
            String corpo;
            if (chamada == 1) {
                status = 429;
                corpo = "Too many requests, please try again later.";
                troca.getResponseHeaders().add("Retry-After", "0");
            } else if (caminho.equals("/api/auth")) {
                status = 200;
                corpo = "{\"token\":\"" + tokenFalso() + "\"}";
            } else {
                status = 200;
                corpo = "[]";
            }
            troca.getResponseHeaders().add("Content-Type", status == 200 ? "application/json" : "text/plain");
            byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
            troca.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = troca.getResponseBody()) {
                os.write(bytes);
            }
        });
        SERVER.start();
    }

    @DynamicPropertySource
    static void propriedades(DynamicPropertyRegistry registro) {
        String url = "http://127.0.0.1:" + SERVER.getAddress().getPort();
        registro.add("uniproof.api.restUrl", () -> url);
        registro.add("uniproof.api.serviceUrl", () -> url);
        registro.add("uniproof.api.notary", () -> "notary-token");
        registro.add("uniproof.api.admin", () -> "admin-token");
        registro.add("uniproof.api.login.email", () -> "service@example.com");
        registro.add("uniproof.api.login.pass", () -> "secret");
    }

    @AfterAll
    static void pararServidor() {
        SERVER.stop(0);
    }

    @Autowired
    private UniproofNotaryClient client;

    @Test
    void tokenAndApiCallsRejectedWith429AreSentAgain() {
        ResponseEntity<List<Attachment>> resposta = client.getAttachmentFromLotItem("lot-1", "notary-token");

        assertEquals(200, resposta.getStatusCode().value());
        assertTrue(resposta.getBody().isEmpty());
        // login (RestTemplate path) and the Feign call were each rejected once, then succeeded
        assertEquals(2, CHAMADAS.get("/api/auth").get());
        assertEquals(2, CHAMADAS.get("/notaries/lot_items/lot-1/attachments").get());
    }

    private static String tokenFalso() {
        long agora = System.currentTimeMillis() / 1000;
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        String cabecalho = enc.encodeToString("{\"alg\":\"none\"}".getBytes(StandardCharsets.UTF_8));
        String payload = enc.encodeToString(("{\"iat\":" + agora + ",\"exp\":" + (agora + 3600) + "}")
                .getBytes(StandardCharsets.UTF_8));
        return cabecalho + "." + payload + ".sig";
    }
}
