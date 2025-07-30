package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.request.CreateContentDto;
import com.umc.sp.contents.dto.response.CategoryDto;
import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentDto;
import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.dto.response.ContentSeriesDetailDto;
import com.umc.sp.contents.dto.response.ContentSeriesDto;
import com.umc.sp.contents.dto.response.GenreDto;
import com.umc.sp.contents.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Category;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentGroup;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.ContentGroupId;
import com.umc.sp.contents.persistence.model.id.ContentId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final GenreMapper genreMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final ContentInfoMapper contentInfoMapper;


    public Content buildContent(final ContentId id, final CreateContentDto createContentDto, final List<Category> categories) {
        return Content.builder()
                      .id(id)
                      .featured(createContentDto.isFeatured())
                      .type(createContentDto.getType())
                      .structureType(createContentDto.getStructureType())
                      .categories(categories)
                      .name(createContentDto.getName())
                      .description(createContentDto.getName())
                      //TODO: add once it is confirmed that will have it
                      //.genre(genre)
                      .build();
    }

    public Optional<ContentDto> convertToDto(final Content content, final Set<UUID> parentIds) {
        if (isNull(content)) {
            return Optional.empty();
        }

        return switch (content.getType()) {
            case SERIES -> Optional.of(convertToContentSeriesDto(content, parentIds));
            default -> Optional.of(getContentDto(content, parentIds));
        };
    }

    public Optional<ContentDetailDto> convertToDetailDto(final Content content, final Set<UUID> parentIds, final List<Tag> contentTags) {
        if (isNull(content)) {
            return Optional.empty();
        }

        var categoryDtos = content.getCategories().stream().map(categoryMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
        var genreDto = genreMapper.convertToDto(content.getGenre());
        return switch (content.getType()) {
            case SERIES -> Optional.of(convertToSeriesDetailDto(content, parentIds, categoryDtos, contentTags, genreDto.orElse(null)));
            default -> Optional.of(getContentDetailDto(content, parentIds, categoryDtos, contentTags, genreDto.orElse(null)));
        };
    }

    public ContentGroup buildContentGroup(final UUID parentId, final ContentId contentId, final int sortOrder) {
        return ContentGroup.builder().id(new ContentGroupId(parentId, contentId.getId())).sortOrder(sortOrder).build();
    }


    private List<TagDto> getTags(final List<Tag> contentTags) {
        if (isEmpty(contentTags)) {
            return new ArrayList<>();
        }
        return contentTags.stream().map(tagMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private List<ContentInfoDto> getAttributes(final List<ContentInfo> attributes) {
        if (isEmpty(attributes)) {
            return new ArrayList<>();
        }
        return attributes.stream().map(contentInfoMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private ContentDto getContentDto(final Content content, final Set<UUID> parentIds) {
        return toContentDto(new ContentDto(), content, parentIds);
    }

    private ContentSeriesDetailDto convertToSeriesDetailDto(final Content content,
                                                            final Set<UUID> parentIds,
                                                            final List<CategoryDto> categoryDtos,
                                                            final List<Tag> contentTags,
                                                            final GenreDto genreDto) {
        return toContentDetailDto(new ContentSeriesDetailDto(), content, parentIds, categoryDtos, contentTags, genreDto);
    }


    private ContentDetailDto getContentDetailDto(final Content content,
                                                 final Set<UUID> parentIds,
                                                 final List<CategoryDto> categoryDtos,
                                                 final List<Tag> contentTags,
                                                 final GenreDto genreDto) {
        return toContentDetailDto(new ContentDetailDto(), content, parentIds, categoryDtos, contentTags, genreDto);
    }

    private ContentSeriesDto convertToContentSeriesDto(final Content content, final Set<UUID> parentIds) {
        return toContentDto(new ContentSeriesDto(), content, parentIds);
    }

    private <T extends ContentDetailDto> T toContentDetailDto(T base,
                                                              final Content content,
                                                              final Set<UUID> parentIds,
                                                              final List<CategoryDto> categoryDtos,
                                                              final List<Tag> contentTags,
                                                              final GenreDto genreDto) {
        base.setId(content.getId().getId());
        base.setParentIds(parentIds);
        base.setFeatured(content.isFeatured());
        base.setType(content.getType());
        base.setStructureType(content.getStructureType());
        base.setCategories(categoryDtos);
        base.setName(content.getName());
        base.setDescription(content.getDescription());
        base.setGenre(genreDto);
        base.setTags(getTags(contentTags));
        base.setAttributes(getAttributes(content.getContentInfos()));
        return base;
    }

    private <T extends ContentDto> T toContentDto(T base, final Content content, final Set<UUID> parentIds) {
        base.setId(content.getId().getId());
        base.setParentIds(parentIds);
        base.setFeatured(content.isFeatured());
        base.setType(content.getType());
        base.setStructureType(content.getStructureType());
        base.setName(content.getName());
        base.setDescription(content.getDescription());
        base.setAttributes(getAttributes(content.getContentInfos()));
        return base;
    }
}
