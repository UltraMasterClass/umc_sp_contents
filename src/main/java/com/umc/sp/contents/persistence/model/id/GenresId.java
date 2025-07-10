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
public class GenresId implements Serializable, Comparable<GenresId> {

    private final UUID id;

    public GenresId() {
        id = UUID.randomUUID();
    }

    public GenresId(String id) {
        this.id = UUID.fromString(id);
    }

    @Override
    public int compareTo(GenresId other) {
        return other == null ? -1 : new CompareToBuilder().append(id, other.id).toComparison();
    }

    @Override
    public String toString() {
        return id.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<GenresId, UUID> {

        @Override
        public UUID convertToDatabaseColumn(GenresId id) {
            return nonNull(id) ? id.getId() : null;
        }

        @Override
        public GenresId convertToEntityAttribute(UUID value) {
            return nonNull(value) ? new GenresId(value) : null;
        }
    }
}
