package com.umc.sp.contents.dto.request;

import com.umc.sp.contents.persistence.model.type.ContentStructureType;
import com.umc.sp.contents.persistence.model.type.ContentType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateContentDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private ContentType type;
    @NotNull
    private ContentStructureType structureType;
    private boolean featured;
    @NotEmpty
    private Set<UUID> categories;
    private Set<UUID> parentContents;
    private Set<CreateContentInfoDto> attributes;
    private Set<UUID> tags;
}
