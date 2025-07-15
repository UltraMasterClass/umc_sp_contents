package com.umc.sp.contents.controller;

import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.manager.ContentServiceManager;
import com.umc.sp.contents.persistence.model.id.ContentId;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> searchContent(@RequestParam(required = false) String text,
                                                           @RequestParam(required = false, defaultValue = "es") String langCode,
                                                           @RequestParam(required = false) Set<UUID> tags,
                                                           @RequestParam(required = false) Set<UUID> categories,
                                                           @RequestParam(required = false, defaultValue = "0") int offset,
                                                           @RequestParam(required = false, defaultValue = "20") int limit) {
        return contentServiceManager.searchContent(tags, categories, text, langCode, offset, getLimit(limit))
                                    .map(contentsDto -> ResponseEntity.ok().body(contentsDto));
    }

    @RequestMapping(value = "/{contentId}", method = RequestMethod.GET, produces = "application/json")
    public Mono<ResponseEntity<ContentDetailDto>> getContentById(@PathVariable String contentId) {
        return contentServiceManager.getContentById(new ContentId(contentId)).map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto));
    }

    @RequestMapping(value = "/{contentId}/children", method = RequestMethod.GET, produces = "application/json")
    public Mono<ResponseEntity<ContentsDto>> getContentByParentId(@PathVariable String contentId,
                                                                  @RequestParam(required = false, defaultValue = "0") int offset,
                                                                  @RequestParam(required = false, defaultValue = "20") int limit) {
        return contentServiceManager.getContentByParentId(new ContentId(contentId), offset, getLimit(limit))
                                    .map(contentDetailDto -> ResponseEntity.ok().body(contentDetailDto));
    }

    private int getLimit(final int limit) {
        return (limit > 0) ? limit : 20;
    }
}
