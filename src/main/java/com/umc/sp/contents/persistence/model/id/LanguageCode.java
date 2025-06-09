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
public class LanguageCode implements Serializable, Comparable<LanguageCode> {

    public static final String ES_MX = "es_mx";

    private final String code;

    public LanguageCode() {
        code = ES_MX;
    }

    @Override
    public int compareTo(LanguageCode other) {
        return other == null ? -1 : new CompareToBuilder().append(code, other.code).toComparison();
    }

    @Override
    public String toString() {
        return code;
    }

    @Converter
    public static class DbConverter implements AttributeConverter<LanguageCode, String> {

        @Override
        public String convertToDatabaseColumn(LanguageCode code) {
            return code != null ? code.toString() : null;
        }

        @Override
        public LanguageCode convertToEntityAttribute(String value) {
            return isBlank(value) ? null : new LanguageCode(value);
        }
    }
}
