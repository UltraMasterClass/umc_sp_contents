package com.umc.sp.contents.converter;

import com.umc.sp.contents.controller.dto.response.ContentDetailDto;
import com.umc.sp.contents.controller.dto.response.ContentDto;
import com.umc.sp.contents.controller.dto.response.ContentInfoDto;
import com.umc.sp.contents.controller.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.Tag;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ContentConverter {

    private final GenreConverter genreConverter;
    private final TagConverter tagConverter;
    private final CategoryConverter categoryConverter;
    private final ContentInfoConverter contentInfoConverter;

    public ContentDto convertToDto(final Content content, final UUID parentId) {
        return ContentDto.builder()
                         .id(content.getId().getId())
                         .parentId(parentId)
                         .featured(content.isFeatured())
                         .type(content.getType())
                         .structureType(content.getStructureType())
                         .name(content.getName())
                         .description(content.getDescription())
                         .specialityId(content.getSpecialityId())
                         .attributes(getAttributes(content.getContentInfos()))
                         .build();
    }

    public ContentDetailDto convertToDetailDto(final Content content, final UUID parentId, final List<Tag> contentTags) {
        var categoryDto = categoryConverter.convertToDto(content.getCategory());
        var genreDto = genreConverter.convertToDto(content.getGenre());
        return ContentDetailDto.builder()
                               .id(content.getId().getId())
                               .parentId(parentId)
                               .featured(content.isFeatured())
                               .type(content.getType())
                               .structureType(content.getStructureType())
                               .category(categoryDto)
                               .name(content.getName())
                               .description(content.getDescription())
                               .genre(genreDto)
                               .specialityId(content.getSpecialityId())
                               .tags(getTags(contentTags))
                               .attributes(getAttributes(content.getContentInfos()))
                               .build();
    }

    private List<TagDto> getTags(final List<Tag> contentTags) {
        if (CollectionUtils.isEmpty(contentTags)) {
            return new ArrayList<>();
        }
        return contentTags.stream().map(tagConverter::convertToDto).toList();
    }

    private List<ContentInfoDto> getAttributes(final List<ContentInfo> attributes) {
        if (CollectionUtils.isEmpty(attributes)) {
            return new ArrayList<>();
        }
        return attributes.stream().map(contentInfoConverter::convertToDto).toList();
    }
}
