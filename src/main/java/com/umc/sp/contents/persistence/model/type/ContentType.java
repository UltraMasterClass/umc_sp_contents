package com.umc.sp.contents.persistence.model.type;

import java.util.Set;

public enum ContentType {
    SERIES, EXPERT, VIDEO, PODCAST, BOOK;

    public static final Set<ContentType> GROUP_TYPES = Set.of(SERIES, EXPERT);
    }
