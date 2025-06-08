package com.umc.sp.contents.persistence.model.id;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ContentTagId implements Serializable {
    private UUID contentId;
    private UUID tagId;

    public ContentTagId() {
    }

    public ContentTagId(UUID contentId, UUID tagId) {
        this.contentId = contentId;
        this.tagId = tagId;
    }
}
