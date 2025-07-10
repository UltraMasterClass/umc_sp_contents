package com.umc.sp.contents.persistence.model.id;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ContentGroupId implements Serializable {

    @Column(name = "parent_content_id")
    private UUID parentContentId;

    @Column(name = "content_id")
    private UUID contentId;

    public ContentGroupId(UUID parentContentId, UUID contentId) {
        this.parentContentId = parentContentId;
        this.contentId = contentId;
    }
}
