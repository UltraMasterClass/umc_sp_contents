package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.response.ContentDto;
import com.umc.sp.contents.dto.response.ContentSectionDto;
import com.umc.sp.contents.dto.response.ContentSeriesDto;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.mapper.ContentSectionMapper;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentSection;
import com.umc.sp.contents.persistence.model.ContentSectionCriteria;
import com.umc.sp.contents.persistence.model.custom.ContentCountByParentId;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType;
import com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType;
import com.umc.sp.contents.persistence.model.type.ContentSectionViewType;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.ContentSectionCriteriaRepository;
import com.umc.sp.contents.persistence.repository.ContentSectionsRepository;
import com.umc.sp.contents.persistence.specification.ContentSpecifications;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.umc.sp.contents.config.CacheConfig.CONTENT_SECTION_DTO_CACHE;
import static com.umc.sp.contents.persistence.model.Content.CREATE_DATE_FIELD;
import static com.umc.sp.contents.persistence.model.ContentSection.SORT_ORDER_FIELD;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType.AND;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaRelationType.NOT;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType.CATEGORY;
import static com.umc.sp.contents.persistence.model.type.ContentSectionCriteriaType.TAG;
import static com.umc.sp.contents.persistence.model.type.ContentSectionType.EXPERTS;
import static com.umc.sp.contents.persistence.model.type.ContentSectionType.HERO_CONTENT;
import static com.umc.sp.contents.persistence.model.type.ContentType.SERIES;
import static com.umc.sp.contents.persistence.specification.ContentSpecifications.hasExpertType;
import static com.umc.sp.contents.persistence.specification.ContentSpecifications.isFeatured;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentSectionService {

    private final ContentRepository contentRepository;
    private final ContentSectionsRepository contentSectionsRepository;
    private final ContentSectionCriteriaRepository contentSectionCriteriaRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final ContentMapper contentMapper;
    private final ContentSectionMapper contentSectionMapper;


    @Transactional(readOnly = true)
    public Slice<ContentSection> getContentSections(final ContentSectionViewType viewType, final int offset, final int limit) {
        var pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.ASC, SORT_ORDER_FIELD));
        return contentSectionsRepository.findByViewTypeAndDisabledDateIsNull(viewType, pageable);
    }

    @Cacheable(value = CONTENT_SECTION_DTO_CACHE, key = "#contentSection.id.id")
    @Transactional(readOnly = true)
    public ContentSectionDto getContentSectionDto(final ContentSection contentSection) {
        // 1. Retrieve active criteria
        var criteriaList = contentSectionCriteriaRepository.findByContentSectionIdAndDisabledDateIsNull(contentSection.getId());

        // 2. Group criteria by type and relationType, flattening UUIDs
        var criteriaMap = groupCriteriaByTypeAndRelation(criteriaList);

        // 3. Build dynamic content filter spec
        final Specification<Content> spec = getContentSpecification(contentSection, criteriaMap);

        // 4. Query content
        var contentOffset = 0;
        var page = contentRepository.findAll(spec,
                                             PageRequest.of(contentOffset, contentSection.getNumberOfElements(), Sort.by(CREATE_DATE_FIELD).descending()));
        var contentIds = page.getContent().stream().map(c -> c.getId().getId()).collect(toSet());

        // 5. Resolve parent content ids for enrichment
        var parentMap = getParentContentIdByContentId(contentIds);

        // 6. Map to DTOs
        var contentDtos = page.stream().map(c -> contentMapper.convertToDto(c, parentMap.get(c.getId().getId()))).flatMap(Optional::stream).toList();
        contentDtos = (HERO_CONTENT.equals(contentSection.getContentType())) ? extendResults(contentDtos) : contentDtos;

        // 7. Extract criteria sets
        var tags = getCriteriaSet(criteriaMap, TAG, AND);
        var categories = getCriteriaSet(criteriaMap, CATEGORY, AND);
        var excludeTags = getCriteriaSet(criteriaMap, TAG, NOT);
        var excludeCategories = getCriteriaSet(criteriaMap, CATEGORY, NOT);

        // 8. Build response
        return contentSectionMapper.buildContentSectionDto(contentSection,
                                                           contentDtos,
                                                           page.hasNext(),
                                                           tags,
                                                           categories,
                                                           excludeTags,
                                                           excludeCategories,
                                                           contentOffset,
                                                           contentSection.getNumberOfElements());
    }

    private List<ContentDto> extendResults(final List<ContentDto> contentDtos) {
        var seriesIds = contentDtos.stream().filter(content -> SERIES.equals(content.getType())).map(ContentDto::getId).collect(Collectors.toSet());
        var childCountByParentId = contentGroupRepository.getChildContentCountByParentContentIds(seriesIds)
                                                         .stream()
                                                         .collect(Collectors.toMap(ContentCountByParentId::getParentContentId,
                                                                                   ContentCountByParentId::getChildContentCount,
                                                                                   (s, s2) -> s2));
        return contentDtos.stream().peek(contentDto -> {
            if (contentDto instanceof ContentSeriesDto seriesDto) {
                int episodeCount = childCountByParentId.getOrDefault(seriesDto.getId(), 0);
                seriesDto.setEpisodes(episodeCount);
            }
        }).toList();
    }

    private Specification<Content> getContentSpecification(final ContentSection contentSection,
                                                           final Map<ContentSectionCriteriaType, Map<ContentSectionCriteriaRelationType, Set<UUID>>> criteriaMap) {
        Specification<Content> spec = null;
        if (HERO_CONTENT.equals(contentSection.getContentType())) {
            spec = isFeatured();
        } else if (EXPERTS.equals(contentSection.getContentType())) {
            spec = hasExpertType();
        }
        var criteriaSpec = fromGroupedCriteriaMap(criteriaMap);

        if (isNull(spec)) {
            return criteriaSpec;
        } else if (isNull(criteriaSpec)) {
            return spec;
        }
        return spec.and(criteriaSpec);
    }

    private Map<ContentSectionCriteriaType, Map<ContentSectionCriteriaRelationType, Set<UUID>>> groupCriteriaByTypeAndRelation(final List<ContentSectionCriteria> criteriaList) {
        return criteriaList.stream()
                           .collect(groupingBy(ContentSectionCriteria::getType,
                                               HashMap::new,
                                               groupingBy(ContentSectionCriteria::getRelationType,
                                                          HashMap::new,
                                                          flatMapping(c -> Arrays.stream(c.getReferenceIds().split(","))
                                                                                 .map(String::trim)
                                                                                 .map(UUID::fromString), toSet()))));
    }

    private Set<UUID> getCriteriaSet(final Map<ContentSectionCriteriaType, Map<ContentSectionCriteriaRelationType, Set<UUID>>> map,
                                     final ContentSectionCriteriaType type,
                                     final ContentSectionCriteriaRelationType relationType) {
        return Optional.ofNullable(map.get(type)).map(inner -> inner.get(relationType)).orElse(Collections.emptySet());
    }

    private Map<UUID, Set<UUID>> getParentContentIdByContentId(final Set<UUID> contentIds) {
        return contentGroupRepository.findAllByIdContentIdIn(contentIds)
                                     .stream()
                                     .collect(groupingBy(contentGroup -> contentGroup.getId().getContentId(),
                                                         mapping(contentGroup -> contentGroup.getId().getParentContentId(), toSet())));

    }

    private Specification<Content> fromGroupedCriteriaMap(final Map<ContentSectionCriteriaType, Map<ContentSectionCriteriaRelationType, Set<UUID>>> groupedMap) {
        var specs = new ArrayList<Specification<Content>>();
        groupedMap.forEach((type, relationMap) -> {
            relationMap.forEach((relation, ids) -> {
                if (ids.isEmpty()) {
                    return;
                }

                switch (type) {
                    case CATEGORY -> specs.add(categorySpec(relation, ids));
                    case TAG -> specs.add(tagSpec(relation, ids));
                }
            });
        });

        return specs.stream().reduce(Specification::and).orElse(null);
    }

    private Specification<Content> categorySpec(ContentSectionCriteriaRelationType relationType, Set<UUID> categoryIds) {
        return switch (relationType) {
            case AND -> ContentSpecifications.hasCategories(categoryIds);
            case NOT -> ContentSpecifications.hasCategoriesNotIn(categoryIds);
        };
    }

    private Specification<Content> tagSpec(ContentSectionCriteriaRelationType relationType, Set<UUID> tagIds) {
        return switch (relationType) {
            case AND -> ContentSpecifications.hasTags(tagIds);
            case NOT -> ContentSpecifications.hasTagsNotIn(tagIds);
        };
    }
}
