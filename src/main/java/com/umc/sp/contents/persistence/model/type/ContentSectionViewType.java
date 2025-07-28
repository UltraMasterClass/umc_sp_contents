package com.umc.sp.contents.persistence.model.type;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentSectionViewType {
    DISCOVERY, CLASSES, EVENTS, TRAINING;

    private static final Map<String, ContentSectionViewType> VALUES_BY_NAME = Arrays.stream(values())
                                                                                    .collect(Collectors.toMap(Enum::name, Function.identity()));

    public static Optional<ContentSectionViewType> from(final String value) {
        return Optional.ofNullable(VALUES_BY_NAME.get(value));
    }
}
