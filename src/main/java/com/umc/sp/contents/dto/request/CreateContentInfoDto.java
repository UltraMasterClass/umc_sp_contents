package com.umc.sp.contents.dto.request;

import com.umc.sp.contents.persistence.model.type.ContentInfoType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.builder.HashCodeExclude;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentInfoDto {
    private ContentInfoType type;
    @HashCodeExclude
    private String value;
}
