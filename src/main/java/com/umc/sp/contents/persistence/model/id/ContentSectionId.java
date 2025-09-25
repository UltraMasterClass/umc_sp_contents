package com.umc.sp.contents.persistence.model.id;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.Serializable;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.CompareToBuilder;
import static java.util.Objects.nonNull;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class ContentSectionId implements Serializable, Comparable<ContentSectionId> {

    private final UUID id;

    public ContentSectionId() {
        id = UUID.randomUUID();
    }

    public ContentSectionId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(ContentSectionId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<ContentSectionId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(ContentSectionId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public ContentSectionId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new ContentSectionId(value) : null;
        }
    }
}
