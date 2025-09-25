package com.umc.sp.contents.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentResourcesDto {
    private UUID id;
    private List<ContentInfoDto> resources;
}
