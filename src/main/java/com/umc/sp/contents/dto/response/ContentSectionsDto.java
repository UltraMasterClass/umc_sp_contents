package com.umc.sp.contents.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentSectionViewType;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ContentSectionsDto {
    private ContentSectionViewType viewType;
    private List<ContentSectionDto> sections;
    private boolean hasNext;
}
