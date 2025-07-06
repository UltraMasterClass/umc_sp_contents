package com.umc.sp.contents.converter;

import com.umc.sp.contents.controller.dto.response.ContentInfoDto;
import com.umc.sp.contents.persistence.model.ContentInfo;
import org.springframework.stereotype.Component;

@Component
public class ContentInfoConverter {

    public ContentInfoDto convertToDto(final ContentInfo contentInfo) {
        return ContentInfoDto.builder().id(contentInfo.getId().getId()).type(contentInfo.getType()).value(contentInfo.getValue()).build();
    }
}
