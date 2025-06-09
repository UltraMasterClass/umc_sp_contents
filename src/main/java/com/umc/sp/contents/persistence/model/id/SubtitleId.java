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
public class SubtitleId implements Serializable, Comparable<SubtitleId> {

    private final UUID id;

    public SubtitleId() {
        id = UUID.randomUUID();
    }

    public SubtitleId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(SubtitleId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<SubtitleId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(SubtitleId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public SubtitleId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new SubtitleId(value) : null;
        }
    }
}
