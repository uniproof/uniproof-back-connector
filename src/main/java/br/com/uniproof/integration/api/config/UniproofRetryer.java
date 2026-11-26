package br.com.uniproof.integration.api.config;

import br.com.uniproof.integration.api.beans.UniproofApiException;
import feign.RetryableException;
import feign.Retryer;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * Retry policy of the Uniproof API clients.
 *
 * <p>A 429 is sent again up to {@link UniproofRateLimiter#getMaxRetries()}
 * times without sleeping here: the rate limiter interceptor runs again before
 * the next attempt and waits for the time the API asked for. Any other
 * retryable failure (I/O errors) keeps the previous policy, {@code
 * Retryer.Default(100 ms, 10 s, 10)}.</p>
 *
 * <p>When the attempts run out, a 429 is thrown as the original
 * {@link UniproofApiException}, so callers see the same exception type as for
 * any other API error.</p>
 */
public class UniproofRetryer implements Retryer {

    private final UniproofRateLimiter rateLimiter;
    private final Retryer outrasFalhas = new Retryer.Default(100, SECONDS.toMillis(10), 10);
    private int tentativas429;

    public UniproofRetryer(UniproofRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public void continueOrPropagate(RetryableException e) {
        if (e.status() != UniproofRateLimiter.TOO_MANY_REQUESTS) {
            outrasFalhas.continueOrPropagate(e);
            return;
        }
        if (++tentativas429 > rateLimiter.getMaxRetries()) {
            if (e.getCause() instanceof UniproofApiException original) {
                throw original;
            }
            throw e;
        }
        if (!rateLimiter.isEnabled() && e.retryAfter() != null) {
            // With the limiter off nobody else waits for the API window.
            long espera = e.retryAfter() - System.currentTimeMillis();
            if (espera > 0) {
                try {
                    Thread.sleep(espera);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
            }
        }
    }

    @Override
    public Retryer clone() {
        return new UniproofRetryer(rateLimiter);
    }
}
