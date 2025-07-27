package com.umc.sp.contents.dto.response;

import com.umc.sp.contents.persistence.model.type.ContentSectionType;
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
public class ContentSectionDto {
    private UUID id;
    private ContentSectionType contentType;
    private String title;
    private List<ContentDto> contents;
    private ExplorerDto explorer;
}
