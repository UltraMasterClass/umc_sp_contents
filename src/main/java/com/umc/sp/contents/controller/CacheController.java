package com.umc.sp.contents.controller;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import static com.umc.sp.contents.config.CacheConfig.CONTENT_SECTION_DTO_CACHE;

@RestController
@RequestMapping("/internal/cache")
@RequiredArgsConstructor
public class CacheController {

    private final CacheManager cacheManager;


    @DeleteMapping("/content-sections")
    public Mono<ResponseEntity<Void>> evictAllContentSectionCache() {
        var cache = cacheManager.getCache(CONTENT_SECTION_DTO_CACHE);
        if (cache != null) {
            cache.clear();
        }
        return Mono.just(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/content-sections/{id}")
    public Mono<ResponseEntity<Void>> evictContentSectionCacheById(@PathVariable UUID id) {
        var cache = cacheManager.getCache(CONTENT_SECTION_DTO_CACHE);
        if (cache != null) {
            cache.evict(id);
        }
        return Mono.just(ResponseEntity.noContent().build());
    }

    @DeleteMapping("/all")
    public Mono<ResponseEntity<Void>> evictAllCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            var cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        return Mono.just(ResponseEntity.noContent().build());
    }
}
