package com.umc.sp.contents.service;

import com.umc.sp.contents.dto.request.CreateContentDto;
import com.umc.sp.contents.dto.request.CreateContentInfoDto;
import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentResourcesDto;
import com.umc.sp.contents.dto.response.ContentSeriesDetailDto;
import com.umc.sp.contents.dto.response.ContentsDto;
import com.umc.sp.contents.exception.BadRequestException;
import com.umc.sp.contents.exception.ConflictException;
import com.umc.sp.contents.exception.NotFoundException;
import com.umc.sp.contents.mapper.ContentInfoMapper;
import com.umc.sp.contents.mapper.ContentMapper;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.custom.ContentCountByParentId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import com.umc.sp.contents.persistence.model.type.ContentInfoType;
import com.umc.sp.contents.persistence.repository.ContentGroupRepository;
import com.umc.sp.contents.persistence.repository.ContentRepository;
import com.umc.sp.contents.persistence.repository.TagsRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import static com.umc.sp.contents.persistence.model.type.ContentInfoType.*;
import static com.umc.sp.contents.persistence.model.type.ContentStructureType.EPISODE;
import static com.umc.sp.contents.persistence.model.type.ContentStructureType.NON_PARENT_TYPES;
import static com.umc.sp.contents.persistence.model.type.ContentType.EXPERT;
import static com.umc.sp.contents.persistence.model.type.ContentType.GROUP_TYPES;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    public static final String SMALL_COVER_JPG = "small_cover.jpg";
    public static final String THUMBNAIL_JPG = "thumbnail.jpg";
    public static final String LARGE_COVER_JPG = "large_cover.jpg";
    public static final String LOGO_SVG = "logo.svg";
    public static final String PROFILE_IMAGE_JPG = "profile_image.jpg";

    private final ContentRepository contentRepository;
    private final ContentGroupRepository contentGroupRepository;
    private final TagsRepository tagsRepository;
    private final ContentMapper contentMapper;
    private final ContentInfoMapper contentInfoMapper;
    private final TagService tagService;


    @Value("${content.resource.videos.url:}")
    private String contentResourceVideosUrl;

    @Value("${content.assets.videos.url:}")
    private String contentAssetsVideosUrl;

    @Value("${content.assets.series.url:}")
    private String contentAssetsSeriesUrl;

    @Value("${content.assets.experts.url:}")
    private String contentAssetsExpertsUrl;


    @Transactional
    public ContentResourcesDto createContent(final CreateContentDto createContentDto, final List<Category> categories) {
        validateContentToCreate(createContentDto);
        var contentId = new ContentId();
        if (CollectionUtils.isEmpty(createContentDto.getAttributes())) {
            createContentDto.setAttributes(new HashSet<>());
        }

        var content = contentMapper.buildContent(contentId, createContentDto, categories);
        var generatedDefaultAttributes = generateDefaultAttributes(content, createContentDto);
        var contentInfos = getContentInfos(createContentDto, content);
        contentInfos.addAll(generatedDefaultAttributes);
        content.setContentInfos(contentInfos);
        contentRepository.save(content);

        associateParentContent(createContentDto, contentId);
        tagService.createContentDefaultTags(content);

        var resources = generatedDefaultAttributes.stream().map(contentInfoMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
        return ContentResourcesDto.builder().id(contentId.getId()).resources(resources).build();
    }

    @Transactional(readOnly = true)
    public ContentDetailDto getContentById(final ContentId id) {
        return contentRepository.findById(id).flatMap(content -> {
            var contentTags = tagsRepository.findContentTagsByContentId(content.getId().getId());
            var parentIds = getParentIds(id);
            return contentMapper.convertToDetailDto(content, parentIds, contentTags).map(this::extendContentDetailDto);
        }).orElseThrow(() -> new NotFoundException(id));
    }

    @Transactional(readOnly = true)
    public ContentsDto getContentByParentId(final ContentId parentId, final int offset, final int limit) {
        var pageable = PageRequest.of(offset / limit, limit);
        var contents = contentRepository.findByParentIdAndDisableDateIsNull(parentId.getId(), pageable);

        var contentIds = contents.stream().map(content -> content.getId().getId()).collect(Collectors.toSet());
        var parentContentIdByContentId = getParentContentIdByContentId(contentIds);

        var dtos = contents.stream()
                           .map(content -> contentMapper.convertToDto(content, parentContentIdByContentId.get(content.getId().getId())))
                           .filter(Optional::isPresent)
                           .map(Optional::get)
                           .toList();
        return ContentsDto.builder().contents(dtos).hasNext(contents.hasNext()).build();
    }

    @Transactional(readOnly = true)
    public void checkContentNotParentAndChildrenOfEachOther(Set<UUID> contentIds) {
        if (CollectionUtils.isEmpty(contentIds)) {
            return;
        }

        if (!contentGroupRepository.checkContentNotParentAndChildrenOfEachOther(contentIds)) {
            throw new ConflictException("Given parent contents must not be parents of each other");
        }
    }

    private ContentDetailDto extendContentDetailDto(final ContentDetailDto detailDto) {
        if (detailDto instanceof ContentSeriesDetailDto seriesDetailDto) {
            // Get episode count, defaulting to 0 if no episodes exist
            var childCount = contentGroupRepository.getChildContentCountByParentContentIds(Set.of(detailDto.getId()))
                                                   .stream()
                                                   .map(ContentCountByParentId::getChildContentCount)
                                                   .findFirst()
                                                   .orElse(0); // 0 is valid - series can have no episodes
            seriesDetailDto.setEpisodes(childCount);
            return seriesDetailDto;
        }
        return detailDto;
    }

    private void associateParentContent(final CreateContentDto createContentDto, final ContentId contentId) {
        if (CollectionUtils.isEmpty(createContentDto.getParentContents())) {
            return;
        }
        var collect = createContentDto.getParentContents()
                                      .stream()
                                      .map(parentId -> contentMapper.buildContentGroup(parentId, contentId, createContentDto.getSortOrder()))
                                      .collect(Collectors.toSet());
        contentGroupRepository.saveAll(collect);
    }


    private Set<UUID> getParentIds(final ContentId id) {
        return contentGroupRepository.findByIdContentId(id.getId())
                                     .stream()
                                     .map(contentGroup -> contentGroup.getId().getParentContentId())
                                     .collect(Collectors.toSet());
    }

    private Map<UUID, Set<UUID>> getParentContentIdByContentId(final Set<UUID> contentIds) {
        return contentGroupRepository.findAllByIdContentIdIn(contentIds)
                                     .stream()
                                     .collect(Collectors.groupingBy(contentGroup -> contentGroup.getId().getContentId(),
                                                                    Collectors.mapping(contentGroup -> contentGroup.getId().getParentContentId(),
                                                                                       Collectors.toSet())));

    }

    private ArrayList<ContentInfo> getContentInfos(final CreateContentDto createContentDto, final Content content) {
        var finalContentInfos = new ArrayList<ContentInfo>();
        if (isNotEmpty(createContentDto.getAttributes())) {
            var contentInfos = createContentDto.getAttributes()
                                               .stream()
                                               .map(createContentInfoDto -> contentInfoMapper.buildContentInfo(createContentInfoDto, content))
                                               .toList();
            finalContentInfos.addAll(contentInfos);
        }
        return finalContentInfos;
    }

    private Set<ContentInfo> generateDefaultAttributes(final Content content, final CreateContentDto createContentDto) {
        return switch (createContentDto.getType()) {
            case SERIES -> generateDefaultSeriesContentInfos(content);
            case VIDEO -> generateDefaultVideoContentInfos(content);
            case EXPERT -> Set.of(contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(SMALL_COVER_IMG_URL,
                                                                                               String.format(contentAssetsExpertsUrl, content.getId()) +
                                                                                               PROFILE_IMAGE_JPG), content));
            default -> {
                log.warn("No default attributes supported for content type {}", createContentDto.getType());
                yield new HashSet<>();
            }
        };
    }

    private Set<ContentInfo> generateDefaultVideoContentInfos(final Content content) {
        return Set.of(getSmallCoverImgContentInfo(content, contentAssetsVideosUrl),
                      getThumbnailImgContentInfo(content, contentAssetsVideosUrl),
                      getLargeCoverImgContentInfo(content, contentAssetsVideosUrl),
                      contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(RESOURCE_URL, String.format(contentResourceVideosUrl, content.getId())),
                                                         content));
    }

    private Set<ContentInfo> generateDefaultSeriesContentInfos(final Content content) {
        return Set.of(getSmallCoverImgContentInfo(content, contentAssetsSeriesUrl),
                      getThumbnailImgContentInfo(content, contentAssetsSeriesUrl),
                      getLargeCoverImgContentInfo(content, contentAssetsSeriesUrl),
                      contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(LOGO_IMG_URL,
                                                                                   String.format(contentAssetsSeriesUrl, content.getId()) + LOGO_SVG),
                                                         content));
    }

    private ContentInfo getLargeCoverImgContentInfo(final Content content, final String assetUrl) {
        return contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(LARGE_COVER_IMG_URL, String.format(assetUrl, content.getId()) + LARGE_COVER_JPG),
                                                  content);
    }

    private ContentInfo getThumbnailImgContentInfo(final Content content, final String assetUrl) {
        return contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(THUMBNAIL_IMG_URL, String.format(assetUrl, content.getId()) + THUMBNAIL_JPG),
                                                  content);
    }

    private ContentInfo getSmallCoverImgContentInfo(final Content content, final String assetUrl) {
        return contentInfoMapper.buildContentInfo(buildCreateContentInfoDto(SMALL_COVER_IMG_URL, String.format(assetUrl, content.getId()) + SMALL_COVER_JPG),
                                                  content);
    }

    private CreateContentInfoDto buildCreateContentInfoDto(final ContentInfoType infoType, final String url) {
        return CreateContentInfoDto.builder().type(infoType).value(url).build();
    }


    private void validateContentToCreate(final CreateContentDto createContentDto) {
        var isNonParent = NON_PARENT_TYPES.contains(createContentDto.getStructureType());
        var isGroupType = GROUP_TYPES.contains(createContentDto.getType());
        if ((isNonParent && isGroupType) || (!isNonParent && !isGroupType)) {
            throw new BadRequestException(createContentDto.getStructureType() + " can not be of type " + createContentDto.getType());
        }

        var isParentContentsEmpty = CollectionUtils.isEmpty(createContentDto.getParentContents());
        if (EPISODE.equals(createContentDto.getStructureType()) && isParentContentsEmpty) {
            throw new BadRequestException("EPISODE should have parent");
        }

        if (EXPERT.equals(createContentDto.getType()) && !isParentContentsEmpty) {
            throw new BadRequestException("EXPERTS should not have parent");
        }

        if (isParentContentsEmpty) {
            return;
        }

        var parentContentIds = createContentDto.getParentContents().stream().map(ContentId::new).collect(Collectors.toSet());
        var parents = contentRepository.findAllById(parentContentIds);
        parents.stream().forEach(content -> {
            if (NON_PARENT_TYPES.contains(content.getStructureType())) {
                throw new BadRequestException(content.getStructureType() + " should not be parent");
            }

            if (!GROUP_TYPES.contains(content.getType())) {
                throw new BadRequestException(content.getType() + " should not be parent");
            }
        });

    }
}
