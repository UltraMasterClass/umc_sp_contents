package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.persistence.model.ContentInfo;
import org.springframework.stereotype.Component;

@Component
public class ContentInfoMapper {

    public ContentInfoDto convertToDto(final ContentInfo contentInfo) {
        return ContentInfoDto.builder().id(contentInfo.getId().getId()).type(contentInfo.getType()).value(contentInfo.getValue()).build();
    }
}
