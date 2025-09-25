package com.umc.sp.contents.persistence.model.id;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.CompareToBuilder;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public class CountryCode implements Serializable, Comparable<CountryCode> {

    public static final String MX = "MX";

    private final String code;

    public CountryCode() {
        code = MX;
    }

    @Override
    public int compareTo(CountryCode other) {
        return other == null ? -1 : new CompareToBuilder().append(code, other.code).toComparison();
    }

    @Override
    public String toString() {
        return code;
    }

    @Converter
    public static class DbConverter implements AttributeConverter<CountryCode, String> {

        @Override
        public String convertToDatabaseColumn(CountryCode code) {
            return code != null ? code.toString() : null;
        }

        @Override
        public CountryCode convertToEntityAttribute(String value) {
            return isBlank(value) ? null : new CountryCode(value);
        }
    }
}
