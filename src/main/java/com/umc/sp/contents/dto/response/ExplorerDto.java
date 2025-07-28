package com.umc.sp.contents.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentSectionType;
import jakarta.validation.constraints.Min;
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
public class ExplorerDto {
    private ContentSectionType contentSectionType;
    private Set<UUID> tags;
    private Set<UUID> categories;
    private Set<UUID> excludeTags;
    private Set<UUID> excludeCategories;
    @Min(0)
    private int offset;
    @Min(1)
    private int limit;
}
