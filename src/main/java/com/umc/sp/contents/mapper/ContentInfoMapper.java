package com.umc.sp.contents.mapper;

import com.umc.sp.contents.dto.response.ContentInfoDto;
import com.umc.sp.contents.persistence.model.ContentInfo;
import java.util.Optional;
import org.springframework.stereotype.Component;
import static java.util.Objects.isNull;

@Component
public class ContentInfoMapper {

    public Optional<ContentInfoDto> convertToDto(final ContentInfo contentInfo) {
        if (isNull(contentInfo)) {
            return Optional.empty();
        }
        return Optional.of(ContentInfoDto.builder().id(contentInfo.getId().getId()).type(contentInfo.getType()).value(contentInfo.getValue()).build());
    }
}
