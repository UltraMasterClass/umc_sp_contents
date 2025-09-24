package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.specification.ContentSpecifications;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import static org.apache.commons.collections4.CollectionUtils.*;
import static org.apache.commons.lang3.StringUtils.*;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "content.search.strategy.db.enabled", havingValue = "true", matchIfMissing = true)
public class ContentSearchServiceDB implements ContentSearchService {

    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final ContentMapper contentMapper;

    @Override
    public ContentsDto searchContent(final Set<UUID> tagCodes,
                                     final Set<UUID> categoryIds,
                                     final String search,
                                     final String langCode,
                                     final int offset,
                                     final int limit) {
        return searchContent(tagCodes, categoryIds, null, null, search, langCode, offset, limit);
    }
    
    @Override
    public ContentsDto searchContent(final Set<UUID> tagCodes,
                                     final Set<UUID> categoryIds,
                                     final Set<UUID> excludeTags,
                                     final Set<UUID> excludeCategories,
                                     final String search,
                                     final String langCode,
                                     final int offset,
                                     final int limit) {
        var pageable = PageRequest.of(offset / limit, limit, Sort.by("name").ascending());
        var contentSpecification = buildSearchQuery(tagCodes, categoryIds, excludeTags, excludeCategories, search, langCode);
        var contents = contentRepository.findAll(contentSpecification, pageable);
        return ContentsDto.builder().contents(getContentDtos(contents)).hasNext(contents.hasNext()).build();
    }


    private List<ContentDto> getContentDtos(final Page<Content> contents) {
        var contentIds = contents.stream().map(Content::getId).map(ContentId::getId).collect(Collectors.toSet());
        var parentContentIdByContentId = getParentContentIdByContentId(contentIds);
        return contents.stream()
                       .map(content -> contentMapper.convertToDto(content, parentContentIdByContentId.get(content.getId().getId())))
                       .filter(Optional::isPresent)
                       .map(Optional::get)
                       .toList();
    }

    //TODO: put on a common place as it is used on ContentService as well
    private Map<UUID, Set<UUID>> getParentContentIdByContentId(final Set<UUID> contentIds) {
        return contentGroupRepository.findAllByIdContentIdIn(contentIds)
                                     .stream()
                                     .collect(Collectors.groupingBy(contentGroup -> contentGroup.getId().getContentId(),
                                                                    Collectors.mapping(contentGroup -> contentGroup.getId().getParentContentId(),
                                                                                       Collectors.toSet())));
    }

    private Specification<Content> buildSearchQuery(final Set<UUID> tagCodes, final Set<UUID> categoryIds, final String search, final String langCode) {
        return buildSearchQuery(tagCodes, categoryIds, null, null, search, langCode);
    }
    
    private Specification<Content> buildSearchQuery(final Set<UUID> tagCodes, 
                                                    final Set<UUID> categoryIds,
                                                    final Set<UUID> excludeTags,
                                                    final Set<UUID> excludeCategories,
                                                    final String search, 
                                                    final String langCode) {
        Specification<Content> spec = (root, query, cb) -> cb.conjunction();
        if (isNotEmpty(tagCodes)) {
            spec = spec.and(ContentSpecifications.hasTags(tagCodes));
        }
        if (isNotEmpty(categoryIds)) {
            spec = spec.and(ContentSpecifications.hasCategories(categoryIds));
        }
        if (isNotEmpty(excludeTags)) {
            spec = spec.and(ContentSpecifications.hasTagsNotIn(excludeTags));
        }
        if (isNotEmpty(excludeCategories)) {
            spec = spec.and(ContentSpecifications.hasCategoriesNotIn(excludeCategories));
        }
        if (isNotBlank(search)) {
            spec = spec.and(ContentSpecifications.searchOnTitleOrCategoryOrTagContains(search, langCode));
        }
        return spec;
    }

}
