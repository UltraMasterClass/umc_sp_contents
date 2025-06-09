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
public class ContentId implements Serializable, Comparable<ContentId> {

    private final UUID id;

    public ContentId() {
        id = UUID.randomUUID();
    }

    public ContentId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(ContentId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<ContentId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(ContentId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public ContentId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new ContentId(value) : null;
        }
    }
}
