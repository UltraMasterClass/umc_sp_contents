package com.umc.sp.contents.persistence.model.custom;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContentCountByParentId {
    private Integer childContentCount;
    private UUID parentContentId;
}
