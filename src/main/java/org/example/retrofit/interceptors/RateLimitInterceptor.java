package org.example.retrofit.interceptors;

import jakarta.annotation.Nonnull;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Super optional class, but another way we could handle GitHub rate limits. We could implement
 * our own bucket/token solution but for simple use cases, we can rely on the timestamps provided via the response
 * headers.
 */
public class RateLimitInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitInterceptor.class);

    private final String RATE_LIMIT_HEADER = "x-ratelimit-reset"; // in milli epoch time (UTC)

    private final AtomicBoolean isRateLimited = new AtomicBoolean(false);
    private final AtomicReference<Instant> rateLimitReset = new AtomicReference<>();

    @Override
    @Nonnull
    public Response intercept(@Nonnull Chain chain) throws IOException {
        if (isRateLimited.get()) {
            if (rateLimitReset.get().isAfter(Instant.now())) {
                throw new TooManyRequestsException();
            }
            isRateLimited.set(false);
        }

        Request request = chain.request();
        Response response = chain.proceed(request);

        if (response.code() == 429) {
            logger.warn("Rate limiting detected. Throwing canned rejection: TOO_MANY_REQUESTS");
            long resetTimestamp = Long.parseLong(Objects.requireNonNull(response.header(RATE_LIMIT_HEADER)));

            // only set this to true after we successfully parse a valid reset timestamp
            isRateLimited.set(true);
            Instant resetTime = Instant.ofEpochSecond(resetTimestamp);
            rateLimitReset.set(resetTime);

            throw new TooManyRequestsException();
        }

        // log statement not needed, but ok for testing purposes
        logger.info("Sanity check that interceptor is allowing non rate limited requests through");
        return response;
    }

    @ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
    public static class TooManyRequestsException extends RuntimeException {}
}
