package br.com.uniproof.integration.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.Function;
import java.util.function.LongSupplier;
import java.util.function.Supplier;

/**
 * Keeps this process inside the Uniproof API rate limits.
 *
 * <p>The API (uniproof-api, {@code express-rate-limit}) applies two limits per
 * client IP, both over fixed windows of {@code RATE_LIMIT_PERIOD} (1 s):</p>
 * <ul>
 *   <li>{@code RATE_LIMIT_MAX} requests of any kind (50 in prod, 25 in dev and stage);</li>
 *   <li>{@code RATE_LIMIT_ERROR_MAX} responses with status 400 or above (5 by default).</li>
 * </ul>
 * <p>Going over either one returns 429 to every request until the window ends.
 * The second limit is the easy one to hit: lookups that answer 404, duplicate
 * uploads that answer 409 and the like are normal for the connectors, and a
 * burst of five of them blocks everything else for a second.</p>
 *
 * <p>Every request to the API calls {@link #acquire()}, which waits until a
 * sliding window of the same length holds fewer than {@code max-requests}
 * requests and fewer than {@code max-errors} error responses. A sliding window
 * that stays under the limit also stays under it in any fixed window, and the
 * defaults sit below the API values to absorb network jitter. A 429 that still
 * gets through (for example, another process sharing the same outbound IP)
 * pauses every caller until the time the API sent in {@code Retry-After}; the
 * request is then sent again, which is safe for any method because the API
 * rejects it before reaching the handler.</p>
 *
 * <p>The state is per JVM. Each dyno has its own outbound IP, which is what the
 * API counts. The bean is declared in {@link UniproofApiConfig}, which every
 * consumer already loads (by component scan or by {@code @Import}), with the
 * {@code uniproof.api.rate-limit.*} properties.</p>
 */
@Slf4j
public class UniproofRateLimiter {

    static final int TOO_MANY_REQUESTS = 429;

    private final boolean enabled;
    private final int maxRequests;
    private final int maxErrors;
    private final long windowMs;
    private final int maxRetries;
    private final LongSupplier clock;
    private final Sleeper sleeper;

    private final Deque<Long> requests = new ArrayDeque<>();
    private final Deque<Long> errors = new ArrayDeque<>();
    private long pausedUntil;

    public UniproofRateLimiter(boolean enabled, int maxRequests, int maxErrors, long windowMs, int maxRetries) {
        this(enabled, maxRequests, maxErrors, windowMs, maxRetries, System::currentTimeMillis, Thread::sleep);
    }

    UniproofRateLimiter(boolean enabled, int maxRequests, int maxErrors, long windowMs, int maxRetries,
                        LongSupplier clock, Sleeper sleeper) {
        if (maxRequests < 1 || maxErrors < 1 || windowMs < 1 || maxRetries < 0) {
            throw new IllegalArgumentException("Invalid uniproof.api.rate-limit settings");
        }
        this.enabled = enabled;
        this.maxRequests = maxRequests;
        this.maxErrors = maxErrors;
        this.windowMs = windowMs;
        this.maxRetries = maxRetries;
        this.clock = clock;
        this.sleeper = sleeper;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** How many times a request rejected with 429 is sent again before giving up. */
    public int getMaxRetries() {
        return maxRetries;
    }

    /**
     * Blocks until a request can be sent without going over the API limits, and
     * counts it.
     */
    public void acquire() {
        if (!enabled) {
            return;
        }
        while (true) {
            long espera;
            synchronized (this) {
                long agora = clock.getAsLong();
                descartarForaDaJanela(agora);
                espera = esperaNecessaria(agora);
                if (espera <= 0) {
                    requests.addLast(agora);
                    return;
                }
            }
            dormir(espera);
        }
    }

    /**
     * Counts a response with status 400 or above against the error budget. A
     * 429 also pauses every caller until {@code retryAt} (epoch millis).
     */
    public void recordError(int status, long retryAt) {
        if (!enabled || status < 400) {
            return;
        }
        synchronized (this) {
            errors.addLast(clock.getAsLong());
            if (status == TOO_MANY_REQUESTS && retryAt > pausedUntil) {
                pausedUntil = retryAt;
                log.warn("API Uniproof answered 429; pausing calls for {} ms", retryAt - clock.getAsLong());
            }
        }
    }

    /**
     * When to try again after a 429, in epoch millis: {@code Retry-After}
     * (seconds or HTTP date), else {@code X-RateLimit-Reset} (epoch seconds),
     * else one window from now.
     *
     * @param header reads a response header by name, ignoring case; returns null when absent
     */
    public long retryAt(Function<String, String> header) {
        long agora = clock.getAsLong();
        String retryAfter = header.apply("Retry-After");
        if (retryAfter != null && !retryAfter.isBlank()) {
            String valor = retryAfter.strip();
            try {
                return agora + Math.max(0, Long.parseLong(valor)) * 1000;
            } catch (NumberFormatException e) {
                try {
                    return ZonedDateTime.parse(valor, DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli();
                } catch (Exception ignorado) {
                    // falls through to the next header
                }
            }
        }
        String reset = header.apply("X-RateLimit-Reset");
        if (reset != null && !reset.isBlank()) {
            try {
                return Long.parseLong(reset.strip()) * 1000;
            } catch (NumberFormatException ignorado) {
                // falls through to the default
            }
        }
        return agora + windowMs;
    }

    /**
     * Runs a call made outside Feign (RestTemplate) under the same limits:
     * waits for a slot, counts error responses and sends it again on 429.
     */
    public <T> T execute(Supplier<T> chamada) {
        for (int tentativa = 0; ; tentativa++) {
            acquire();
            try {
                return chamada.get();
            } catch (HttpStatusCodeException e) {
                int status = e.getStatusCode().value();
                recordError(status, retryAt(nome -> e.getResponseHeaders() != null
                        ? e.getResponseHeaders().getFirst(nome) : null));
                if (status != TOO_MANY_REQUESTS || tentativa >= maxRetries) {
                    throw e;
                }
                log.info("API Uniproof answered 429; sending the request again (attempt {} of {})",
                        tentativa + 1, maxRetries);
            }
        }
    }

    private void descartarForaDaJanela(long agora) {
        long limite = agora - windowMs;
        while (!requests.isEmpty() && requests.peekFirst() <= limite) {
            requests.removeFirst();
        }
        while (!errors.isEmpty() && errors.peekFirst() <= limite) {
            errors.removeFirst();
        }
    }

    private long esperaNecessaria(long agora) {
        long espera = pausedUntil - agora;
        if (requests.size() >= maxRequests) {
            espera = Math.max(espera, requests.peekFirst() + windowMs - agora);
        }
        if (errors.size() >= maxErrors) {
            espera = Math.max(espera, errors.peekFirst() + windowMs - agora);
        }
        return espera;
    }

    private void dormir(long ms) {
        try {
            sleeper.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for the Uniproof API rate limit", e);
        }
    }

    @FunctionalInterface
    interface Sleeper {
        void sleep(long ms) throws InterruptedException;
    }
}
