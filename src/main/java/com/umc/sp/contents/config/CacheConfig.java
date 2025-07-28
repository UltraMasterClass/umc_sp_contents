package com.umc.sp.contents.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import java.util.List;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    public static final String CONTENT_SECTION_DTO_CACHE = "contentSectionDtoCache";

    @Bean
    public CacheManager cacheManager() {
        var contentSectionCache = new CaffeineCache(CONTENT_SECTION_DTO_CACHE,
                                                    Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(12)).maximumSize(500).build());

        // add more needed caches here :)

        var cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(List.of(contentSectionCache));
        return cacheManager;
    }

}
