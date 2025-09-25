package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.Tag;
import com.umc.sp.contents.persistence.model.id.TagId;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
public class TagMapper {

    public Optional<TagDto> convertToDto(final Tag tag) {
        if (isNull(tag)) {
            return Optional.empty();
        }
        return Optional.of(TagDto.builder().id(tag.getId().getId()).code(tag.getCode()).description(tag.getDescription()).build());
    }


    public Tag fromContent(final Content content) {
        return Tag.builder().id(new TagId()).code(content.getName()).description(content.getDescription()).build();
    }
}
