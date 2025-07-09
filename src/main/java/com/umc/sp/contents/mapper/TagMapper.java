package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagDto convertToDto(final Tag tag) {
        return TagDto.builder().id(tag.getId().getId()).code(tag.getCode()).description(tag.getDescription()).build();
    }
}
