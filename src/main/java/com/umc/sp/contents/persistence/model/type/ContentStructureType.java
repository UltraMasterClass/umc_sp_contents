package com.umc.sp.contents.persistence.model.type;

import java.util.Set;

public enum ContentStructureType {
    GROUP, EPISODE, INDIVIDUAL;

    public static final Set<ContentStructureType> NON_PARENT_TYPES = Set.of(EPISODE, INDIVIDUAL);
}
