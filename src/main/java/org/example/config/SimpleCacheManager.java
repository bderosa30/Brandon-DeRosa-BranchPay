package org.example.config;

import com.google.common.cache.CacheBuilder;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Simple caching to help against rate limiting by GitHub APIs. This is intended
 * for that purposes, so we have a more aggressive eviction policy. If time permits,
 * using a standalone service like Redis would be preferrable.
 */
@EnableCaching
@Configuration
public class SimpleCacheManager implements CachingConfigurer {

    public static final String USER_DETAILS_CACHE = "user-details";

    @Bean
    @Override
    public CacheManager cacheManager() {
        // roughly 100KB per 2 api calls * 1000 is roughly 100MB in-mem storage. reasonable number but
        // better to use a dedicated service like memcached or Redis.
        return new ConcurrentMapCacheManager(USER_DETAILS_CACHE) {
            @Override
            @Nonnull
            protected Cache createConcurrentMapCache(@NonNull String name) {
                return new ConcurrentMapCache(
                        name,
                        // 60 minute cache eviction to match rate-limit by github apis
                        CacheBuilder.newBuilder()
                                .expireAfterWrite(60, TimeUnit.MINUTES)
                                .maximumSize(1000)
                                .build()
                                .asMap(),
                        false
                );
            }
        };
    }
}
