package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.request.CreateContentInfoDto;
import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.persistence.model.Content;
import com.umc.sp.contents.persistence.model.ContentInfo;
import com.umc.sp.contents.persistence.model.id.ContentInfoId;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
public class ContentInfoMapper {

    public ContentInfo buildContentInfo(final CreateContentInfoDto createContentInfoDto, final Content content) {
        return ContentInfo.builder()
                          .id(new ContentInfoId())
                          .type(createContentInfoDto.getType())
                          .value(createContentInfoDto.getValue())
                          .content(content)
                          .build();
    }

    public Optional<ContentInfoDto> convertToDto(final ContentInfo contentInfo) {
        if (isNull(contentInfo)) {
            return Optional.empty();
        }
        return Optional.of(ContentInfoDto.builder().id(contentInfo.getId().getId()).type(contentInfo.getType()).value(contentInfo.getValue()).build());
    }
}
