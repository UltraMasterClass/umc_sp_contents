package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.ContentDto;
import com.umc.sp.contents.dto.response.ContentSectionDto;
import com.umc.sp.contents.dto.response.ExplorerDto;
import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ContentSectionMapper {

    public ContentSectionDto buildContentSectionDto(final ContentSection contentSection,
                                                    final List<ContentDto> dtos,
                                                    final boolean hasMoreContent,
                                                    final Set<UUID> tags,
                                                    final Set<UUID> categories,
                                                    final Set<UUID> excludeTags,
                                                    final Set<UUID> excludeCategories,
                                                    final int offset,
                                                    final int limit) {
        return ContentSectionDto.builder()
                                .id(contentSection.getId().getId())
                                .contentType(contentSection.getContentType())
                                .title(contentSection.getTitle())
                                .contents(dtos)
                                .explorer(getExplorer(contentSection.getContentType(),
                                                      hasMoreContent,
                                                      tags,
                                                      categories,
                                                      excludeTags,
                                                      excludeCategories,
                                                      offset,
                                                      limit))
                                .build();
    }

    private ExplorerDto getExplorer(final ContentSectionType contentSectionType,
                                    final boolean hasMoreContent,
                                    final Set<UUID> tags,
                                    final Set<UUID> categories,
                                    final Set<UUID> excludeTags,
                                    final Set<UUID> excludeCategories,
                                    final int offset,
                                    final int limit) {
        if (!hasMoreContent) {
            return null;
        }

        return ExplorerDto.builder()
                          .contentSectionType(contentSectionType)
                          .tags(tags)
                          .categories(categories)
                          .excludeTags(excludeTags)
                          .excludeCategories(excludeCategories)
                          .offset(offset)
                          .limit(limit)
                          .build();
    }
}
