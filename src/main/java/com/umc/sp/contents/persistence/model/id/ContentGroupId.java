package com.umc.sp.contents.persistence.model.id;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class ContentGroupId implements Serializable {
    private UUID contentId;
    private UUID groupId;

    public ContentGroupId() {
    }

    public ContentGroupId(UUID contentId, UUID groupId) {
        this.contentId = contentId;
        this.groupId = groupId;
    }
}
