package br.com.uniproof.integration.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UniproofRateLimiterTest {

    private static final long INICIO = 1_000_000L;

    private final AtomicLong agora = new AtomicLong(INICIO);
    private final List<Long> esperas = new ArrayList<>();

    private UniproofRateLimiter limiter;

    @BeforeEach
    void setUp() {
        limiter = novo(3, 2, 2);
    }

    private UniproofRateLimiter novo(int maxRequests, int maxErrors, int maxRetries) {
        return new UniproofRateLimiter(true, maxRequests, maxErrors, 1000, maxRetries, agora::get, ms -> {
            esperas.add(ms);
            agora.addAndGet(ms);
        });
    }

    @Test
    void requestsOverTheLimitWaitForTheWindowToMove() {
        limiter.acquire();
        agora.addAndGet(100);
        limiter.acquire();
        limiter.acquire();
        assertTrue(esperas.isEmpty());

        limiter.acquire();

        // the oldest request (at INICIO) leaves the window at INICIO + 1000
        assertEquals(List.of(900L), esperas);
        assertEquals(INICIO + 1000, agora.get());
    }

    @Test
    void errorResponsesOverTheBudgetHoldTheNextRequest() {
        limiter.acquire();
        limiter.recordError(404, 0);
        agora.addAndGet(200);
        limiter.recordError(409, 0);

        limiter.acquire();

        assertEquals(List.of(800L), esperas);
    }

    @Test
    void successfulOrRedirectStatusesDoNotCountAsErrors() {
        limiter.recordError(302, 0);
        limiter.recordError(399, 0);
        limiter.recordError(302, 0);

        limiter.acquire();

        assertTrue(esperas.isEmpty());
    }

    @Test
    void tooManyRequestsPausesEveryCallerUntilRetryAt() {
        limiter.recordError(429, INICIO + 1500);

        limiter.acquire();

        assertEquals(INICIO + 1500, agora.get());
    }

    @Test
    void disabledLimiterNeverWaits() {
        UniproofRateLimiter desligado = new UniproofRateLimiter(false, 1, 1, 1000, 0, agora::get,
                ms -> esperas.add(ms));
        desligado.recordError(429, INICIO + 5000);
        for (int i = 0; i < 5; i++) {
            desligado.acquire();
        }
        assertTrue(esperas.isEmpty());
    }

    @Test
    void retryAtReadsRetryAfterSeconds() {
        assertEquals(INICIO + 2000, limiter.retryAt(Map.of("Retry-After", "2")::get));
    }

    @Test
    void retryAtReadsRetryAfterHttpDate() {
        assertEquals(1_700_000_000_000L,
                limiter.retryAt(Map.of("Retry-After", "Tue, 14 Nov 2023 22:13:20 GMT")::get));
    }

    @Test
    void retryAtFallsBackToRateLimitResetThenToOneWindow() {
        assertEquals(1_700_000_000_000L, limiter.retryAt(Map.of("X-RateLimit-Reset", "1700000000")::get));
        assertEquals(INICIO + 1000, limiter.retryAt(Map.<String, String>of()::get));
    }

    @Test
    void executeSendsAgainAfterTooManyRequests() {
        AtomicInteger chamadas = new AtomicInteger();

        String resultado = limiter.execute(() -> {
            if (chamadas.incrementAndGet() == 1) {
                throw tooManyRequests("1");
            }
            return "ok";
        });

        assertEquals("ok", resultado);
        assertEquals(2, chamadas.get());
        assertEquals(INICIO + 1000, agora.get());
    }

    @Test
    void executeGivesUpAfterMaxRetries() {
        AtomicInteger chamadas = new AtomicInteger();

        assertThrows(HttpClientErrorException.TooManyRequests.class, () -> limiter.execute(() -> {
            chamadas.incrementAndGet();
            throw tooManyRequests("0");
        }));

        // first attempt plus maxRetries (2)
        assertEquals(3, chamadas.get());
    }

    @Test
    void executeDoesNotRetryOtherErrorsButCountsThem() {
        UniproofRateLimiter umErro = novo(10, 1, 2);
        AtomicInteger chamadas = new AtomicInteger();

        assertThrows(HttpClientErrorException.NotFound.class, () -> umErro.execute(() -> {
            chamadas.incrementAndGet();
            throw HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", HttpHeaders.EMPTY,
                    new byte[0], StandardCharsets.UTF_8);
        }));
        assertEquals(1, chamadas.get());

        // the 404 used the whole error budget: the next request waits for the window
        umErro.acquire();
        assertEquals(List.of(1000L), esperas);
    }

    private static HttpClientErrorException tooManyRequests(String retryAfter) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Retry-After", retryAfter);
        return HttpClientErrorException.create(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests", headers,
                new byte[0], StandardCharsets.UTF_8);
    }
}
