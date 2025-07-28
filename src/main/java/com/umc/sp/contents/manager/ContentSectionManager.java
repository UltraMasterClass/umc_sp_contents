package com.umc.sp.contents.manager;

import com.umc.sp.contents.dto.response.ContentSectionsDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.dto.response.ExplorerDto;
import com.umc.sp.contents.persistence.model.type.ContentSectionViewType;
import com.umc.sp.contents.service.ContentSearchService;
import com.umc.sp.contents.service.ContentSectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ContentSectionManager {

    private final ContentSectionService contentSectionService;
    private final ContentSearchService contentSearchService;

    public Mono<ContentSectionsDto> getContentSections(final ContentSectionViewType viewType, final int offset, final int limit, final String langCode) {
        // TODO: support multiple language codes thru langCode, once translations are available :)
        return Mono.fromCallable(() -> contentSectionService.getContentSections(viewType, offset, limit)).map(contentSections -> {
            var sectionDtos = contentSections.stream().parallel().map(contentSectionService::getContentSectionDto).toList();
            return ContentSectionsDto.builder().viewType(viewType).sections(sectionDtos).hasNext(contentSections.hasNext()).build();
        });
    }

    public Mono<ContentsDto> searchByExplorer(final ExplorerDto explorerDto, final String langCode) {
        // TODO: support excluded tags and categories on the search content
        return Mono.just(contentSearchService.searchContent(explorerDto.getTags(),
                                                            explorerDto.getCategories(),
                                                            null,
                                                            langCode,
                                                            explorerDto.getOffset(),
                                                            explorerDto.getLimit()));
    }
}
