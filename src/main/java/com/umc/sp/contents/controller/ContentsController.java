package com.umc.sp.contents.controller;

import com.umc.sp.contents.dto.request.CreateContentDto;
import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentResourcesDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.manager.ContentServiceManager;
import com.umc.sp.contents.persistence.model.id.ContentId;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@Validated
@RestController
@RequestMapping("/content")
@RequiredArgsConstructor
public class ContentsController {

    private final ContentServiceManager contentServiceManager;

    @GetMapping(value = "/search", produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> searchContent(@RequestParam(required = false) String text,
                                                           //TODO: support as header or param? :O
                                                           @RequestParam(required = false, defaultValue = "es") String langCode,
                                                           @RequestParam(required = false) Set<UUID> tags,
                                                           @RequestParam(required = false) Set<UUID> categories,
                                                           @RequestParam(required = false, defaultValue = "0") int offset,
                                                           @RequestParam(required = false, defaultValue = "20") int limit) {
        return contentServiceManager.searchContent(tags, categories, text, langCode, offset, getLimit(limit))
                                    .map(contentsDto -> ResponseEntity.ok().body(contentsDto));
    }

    @GetMapping(value = "/{contentId}", produces = "application/json")
    public Mono<ResponseEntity<ContentDetailDto>> getContentById(@PathVariable String contentId) {
        return contentServiceManager.getContentById(new ContentId(contentId)).map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto));
    }

    @GetMapping(value = "/{contentId}/children", produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> getContentByParentId(@PathVariable String contentId,
                                                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                                                  @RequestParam(required = false, defaultValue = "20") int limit) {
        return contentServiceManager.getContentByParentId(new ContentId(contentId), offset, getLimit(limit))
                                    .map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto));
    }

    @PostMapping(produces = "application/json")
    public Mono<ResponseEntity<ContentResourcesDto>> createContent(@RequestBody CreateContentDto createContentDto) {
        return contentServiceManager.createContent(createContentDto).map(contentResourcesDto -> ResponseEntity.ok().body(contentResourcesDto));
    }

    private int getLimit(final int limit) {
        return (limit > 0) ? limit : 20;
    }
}
