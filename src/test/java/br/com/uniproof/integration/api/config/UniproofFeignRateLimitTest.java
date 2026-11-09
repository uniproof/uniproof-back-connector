package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.beans.UniproofApiException;
import com.sun.net.httpserver.HttpServer;
import feign.Feign;
import feign.RequestLine;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Wires the decoder, the retryer and the limiter into a real Feign client
 * against a local server that answers like the express-rate-limit of the API.
 */
class UniproofFeignRateLimitTest {

    interface Api {
        @RequestLine("POST /events")
        String post(String body);

        @RequestLine("GET /missing")
        String missing();
    }

    private HttpServer server;
    private final Deque<Integer> respostas = new ArrayDeque<>();
    private final AtomicInteger chamadas = new AtomicInteger();
    private String url;

    @BeforeEach
    void setUp() throws Exception {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", troca -> {
            chamadas.incrementAndGet();
            troca.getRequestBody().readAllBytes();
            Integer status = respostas.isEmpty() ? 200 : respostas.poll();
            byte[] corpo = (status == 429
                    ? "Too many requests, please try again later."
                    : status == 200 ? "ok" : "{\"statusCode\":" + status + ",\"message\":\"Not found\"}")
                    .getBytes(StandardCharsets.UTF_8);
            if (status == 429) {
                // what express-rate-limit sends; 0 keeps the test fast
                troca.getResponseHeaders().add("Retry-After", "0");
                troca.getResponseHeaders().add("X-RateLimit-Limit", "5");
                troca.getResponseHeaders().add("X-RateLimit-Remaining", "0");
            }
            troca.sendResponseHeaders(status, corpo.length);
            try (OutputStream os = troca.getResponseBody()) {
                os.write(corpo);
            }
        });
        server.start();
        url = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    private Api cliente(UniproofRateLimiter limiter) {
        return Feign.builder()
                .errorDecoder(new UniproofApiErrorDecoder(limiter, url))
                .retryer(new UniproofRetryer(limiter))
                .requestInterceptor(template -> limiter.acquire())
                .target(Api.class, url);
    }

    private UniproofRateLimiter limiter(int maxRetries) {
        return new UniproofRateLimiter(true, 20, 4, 1000, maxRetries);
    }

    @Test
    void postRejectedWith429IsSentAgainAndSucceeds() {
        respostas.add(429);
        respostas.add(429);

        String resultado = cliente(limiter(10)).post("{}");

        assertEquals("ok", resultado);
        assertEquals(3, chamadas.get());
    }

    @Test
    void after429RetriesRunOutTheCallerGetsUniproofApiException() {
        for (int i = 0; i < 5; i++) {
            respostas.add(429);
        }

        UniproofApiException erro = assertThrows(UniproofApiException.class,
                () -> cliente(limiter(2)).post("{}"));

        assertEquals(429, erro.getStatusCode());
        assertEquals(3, chamadas.get());
    }

    @Test
    void otherErrorsAreNotRetried() {
        respostas.add(404);

        UniproofApiException erro = assertThrows(UniproofApiException.class, () -> cliente(limiter(10)).missing());

        assertEquals(404, erro.getStatusCode());
        assertEquals(1, chamadas.get());
    }

    @Test
    void burstOfErrorsIsSpreadSoTheApiErrorLimitIsNotReached() {
        UniproofRateLimiter limiter = limiter(10);
        Api api = cliente(limiter);
        for (int i = 0; i < 6; i++) {
            respostas.add(404);
        }

        long inicio = System.currentTimeMillis();
        for (int i = 0; i < 6; i++) {
            assertThrows(UniproofApiException.class, api::missing);
        }

        // 4 errors per second at most: the 5th request had to wait for the window
        assertEquals(6, chamadas.get());
        long decorrido = System.currentTimeMillis() - inicio;
        org.junit.jupiter.api.Assertions.assertTrue(decorrido >= 900, "took " + decorrido + " ms");
    }
}
