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
public class ContentSectionCriteriaId implements Serializable, Comparable<ContentSectionCriteriaId> {

    private final UUID id;

    public ContentSectionCriteriaId() {
        id = UUID.randomUUID();
    }

    public ContentSectionCriteriaId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(ContentSectionCriteriaId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<ContentSectionCriteriaId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(ContentSectionCriteriaId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public ContentSectionCriteriaId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new ContentSectionCriteriaId(value) : null;
        }
    }
}
