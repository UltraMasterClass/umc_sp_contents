package com.umc.sp.contents.controller.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentInfoType;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentInfoDto {
    private UUID id;
    private ContentInfoType type;
    private String value;
}
