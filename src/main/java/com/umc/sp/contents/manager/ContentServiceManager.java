package com.umc.sp.contents.manager;

import com.umc.sp.contents.controller.dto.response.ContentDetailDto;
import com.umc.sp.contents.controller.dto.response.ContentsDto;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.service.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContentServiceManager {

    private final ContentService contentService;

    public Mono<ContentDetailDto> getContentById(final ContentId id) {
        return Mono.just(contentService.getContentById(id));
    }

    public Mono<ContentsDto> getContentByParentId(final ContentId parentId, final int offset, final int limit) {
        return Mono.just(contentService.getContentByParentId(parentId,offset,limit));
    }
}
