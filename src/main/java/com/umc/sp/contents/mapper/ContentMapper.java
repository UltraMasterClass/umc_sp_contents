package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.ContentDetailDto;
import com.umc.sp.contents.dto.response.ContentDto;
import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
@RequiredArgsConstructor
public class ContentMapper {

    private final GenreMapper genreMapper;
    private final TagMapper tagMapper;
    private final CategoryMapper categoryMapper;
    private final ContentInfoMapper contentInfoMapper;

    public Optional<ContentDto> convertToDto(final Content content, final UUID parentId) {
        if (isNull(content)) {
            return Optional.empty();
        }
        return Optional.of(ContentDto.builder()
                                     .id(content.getId().getId())
                                     .parentId(parentId)
                                     .featured(content.isFeatured())
                                     .type(content.getType())
                                     .structureType(content.getStructureType())
                                     .name(content.getName())
                                     .description(content.getDescription())
                                     .specialityId(content.getSpecialityId())
                                     .attributes(getAttributes(content.getContentInfos()))
                                     .build());
    }

    public Optional<ContentDetailDto> convertToDetailDto(final Content content, final UUID parentId, final List<Tag> contentTags) {
        if (isNull(content)) {
            return Optional.empty();
        }

        var categoryDto = categoryMapper.convertToDto(content.getCategory());
        var genreDto = genreMapper.convertToDto(content.getGenre());
        return Optional.of(ContentDetailDto.builder()
                                           .id(content.getId().getId())
                                           .parentId(parentId)
                                           .featured(content.isFeatured())
                                           .type(content.getType())
                                           .structureType(content.getStructureType())
                                           .category(categoryDto.orElse(null))
                                           .name(content.getName())
                                           .description(content.getDescription())
                                           .genre(genreDto.orElse(null))
                                           .specialityId(content.getSpecialityId())
                                           .tags(getTags(contentTags))
                                           .attributes(getAttributes(content.getContentInfos()))
                                           .build());
    }

    private List<TagDto> getTags(final List<Tag> contentTags) {
        if (CollectionUtils.isEmpty(contentTags)) {
            return new ArrayList<>();
        }
        return contentTags.stream().map(tagMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
    }

    private List<ContentInfoDto> getAttributes(final List<ContentInfo> attributes) {
        if (CollectionUtils.isEmpty(attributes)) {
            return new ArrayList<>();
        }
        return attributes.stream().map(contentInfoMapper::convertToDto).filter(Optional::isPresent).map(Optional::get).toList();
    }
}
