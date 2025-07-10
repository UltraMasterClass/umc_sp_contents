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
public class ContentInfoId implements Serializable, Comparable<ContentInfoId> {

    private final UUID id;

    public ContentInfoId() {
        id = UUID.randomUUID();
    }

    public ContentInfoId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(ContentInfoId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<ContentInfoId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(ContentInfoId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public ContentInfoId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new ContentInfoId(value) : null;
        }
    }
}
