package com.umc.sp.contents.controller;

import com.umc.sp.contents.IntegrationTest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.test.web.reactive.server.WebTestClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CacheControllerIntegrationTest implements IntegrationTest {

    private static final String CACHE_NAME = "contentSectionDtoCache";
    private static final UUID SECTION_ID = UUID.randomUUID();

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private CacheManager cacheManager;


    @BeforeEach
    void setUp() {
        var cache = getCache();
        assertNotNull(cache);
        cache.put(SECTION_ID, "dummyContentSection");
    }

    @Test
    void shouldEvictSingleEntryFromCacheById() {
        // given
        assertNotNull(getCache().get(SECTION_ID));

        // when
        webTestClient.delete().uri("/internal/cache/content-sections/{id}", SECTION_ID).exchange().expectStatus().isNoContent();

        // then
        assertThat(getCache().get(SECTION_ID)).isNull();
    }

    @Test
    void shouldEvictAllEntriesFromContentSectionCache() {
        // given
        getCache().put(UUID.randomUUID(), "another");

        // when
        webTestClient.delete().uri("/internal/cache/content-sections").exchange().expectStatus().isNoContent();

        // then
        CaffeineCache caffeineCache = (CaffeineCache) cacheManager.getCache(CACHE_NAME);
        com.github.benmanes.caffeine.cache.Cache<?, ?> nativeCache = caffeineCache.getNativeCache();
        assertThat(nativeCache.estimatedSize()).isEqualTo(0);
    }

    @Test
    void shouldEvictAllCaches() {
        // given
        getCache().put(SECTION_ID, "dummy");

        // when
        webTestClient.delete().uri("/internal/cache/all").exchange().expectStatus().isNoContent();

        // then
        assertThat(getCache().get(SECTION_ID)).isNull();
    }

    private Cache getCache() {
        var cache = cacheManager.getCache(CACHE_NAME);
        assertNotNull(cache, "Expected cache '%s' to exist".formatted(CACHE_NAME));
        return cache;
    }
}
