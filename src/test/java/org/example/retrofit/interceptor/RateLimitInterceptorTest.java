package org.example.retrofit.interceptor;

import okhttp3.Interceptor;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.example.retrofit.interceptors.RateLimitInterceptor;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RateLimitInterceptorTest {

    private Interceptor.Chain chain = mock(Interceptor.Chain.class, RETURNS_DEEP_STUBS);

    private RateLimitInterceptor interceptor = new RateLimitInterceptor();

    @Test
    public void rateLimited() throws Exception {
        // Detect Rate Limiting and throw appropriate error
        Request req = new Request.Builder()
                .url("https://test.com")
                .get()
                .build();

        Response resp = new Response.Builder()
                .code(429)
                .addHeader("x-ratelimit-reset", String.valueOf(Instant.now().plusSeconds(15).getEpochSecond()))
                .request(req)
                .protocol(Protocol.HTTP_1_0)
                .message("rate-limit")
                .build();

        when(chain.proceed(any())).thenReturn(resp);

        assertThatThrownBy(() -> interceptor.intercept(chain))
                .isInstanceOf(RateLimitInterceptor.TooManyRequestsException.class);
    }

    @Test
    public void rateLimited_butNoLonger() throws Exception {
        // First request sets up the rate limiting variables, passing in a reset time in the past
        Request req = new Request.Builder()
                .url("https://test.com")
                .get()
                .build();

        Response resp = new Response.Builder()
                .code(429)
                .addHeader("x-ratelimit-reset", String.valueOf(Instant.now().minusSeconds(15).getEpochSecond()))
                .request(req)
                .protocol(Protocol.HTTP_1_0)
                .message("rate-limit")
                .build();

        when(chain.proceed(any())).thenReturn(resp);

        assertThatThrownBy(() -> interceptor.intercept(chain))
                .isInstanceOf(RateLimitInterceptor.TooManyRequestsException.class);


        // Second request comes through, where we were previously rate limited. We are past the reset
        // time so request should go through
        Response free = new Response.Builder()
                .code(200)
                .request(req)
                .protocol(Protocol.HTTP_1_0)
                .message("rate-limit no longer")
                .build();
        when(chain.proceed(any())).thenReturn(free);

        Response actual = interceptor.intercept(chain);
        assertThat(actual.code()).isEqualTo(200);
    }
}
