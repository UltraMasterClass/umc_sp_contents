package com.umc.sp.contents.controller.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {
    private UUID id;
    private UUID parentId;
    private boolean featured;
    private ContentType type;
    private ContentStructureType structureType;
    private String name;
    private String description;
    private UUID specialityId;
    private List<ContentInfoDto> attributes;
}
