package com.umc.sp.contents.converter;

import com.umc.sp.contents.controller.dto.response.TagDto;
import com.umc.sp.contents.persistence.model.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagConverter {

    public TagDto convertToDto(final Tag tag) {
        return TagDto.builder().id(tag.getId().getId()).code(tag.getCode()).description(tag.getDescription()).build();
    }
}
