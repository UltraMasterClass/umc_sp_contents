package com.umc.sp.contents.persistence.model.id;

import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class GroupTagId implements Serializable {
    private UUID groupId;
    private UUID tagId;

    public GroupTagId() {
    }

    public GroupTagId(UUID groupId, UUID tagId) {
        this.groupId = groupId;
        this.tagId = tagId;
    }
}
