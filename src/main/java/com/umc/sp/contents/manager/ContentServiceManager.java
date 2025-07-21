package com.umc.sp.contents.manager;

import com.umc.sp.contents.dto.request.CreateContentDto;
import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentResourcesDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.service.CategoryService;
import com.umc.sp.contents.service.ContentSearchService;
import com.umc.sp.contents.service.ContentService;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContentServiceManager {

    private final ContentService contentService;
    private final ContentSearchService contentSearchService;
    private final CategoryService categoryService;


    public Mono<ContentResourcesDto> createContent(final CreateContentDto createContentDto) {
        return Mono.fromCallable(() -> {
            categoryService.checkCategoriesNotParentAndChildrenOfEachOther(createContentDto.getCategories());
            return categoryService.getCategoriesByIds(createContentDto.getCategories());
        }).map(categories -> {
            //TODO: validate content types hierarchy, e.g episode should have parent and can't be parent, etc.
            contentService.checkCategoriesNotParentAndChildrenOfEachOther(createContentDto.getParentContents());
            return contentService.createContent(createContentDto, categories);
        });
    }

    public Mono<ContentDetailDto> getContentById(final ContentId id) {
        return Mono.just(contentService.getContentById(id));
    }

    public Mono<ContentsDto> getContentByParentId(final ContentId parentId, final int offset, final int limit) {
        return Mono.just(contentService.getContentByParentId(parentId, offset, limit));
    }

    public Mono<ContentsDto> searchContent(final Set<UUID> tagCodes,
                                           final Set<UUID> categoryIds,
                                           final String search,
                                           final String langCode,
                                           final int offset,
                                           final int limit) {
        return Mono.just(contentSearchService.searchContent(tagCodes, categoryIds, search, langCode, offset, limit));
    }
}
