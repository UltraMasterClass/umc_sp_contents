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
public class CurrencyCode implements Serializable, Comparable<CurrencyCode> {

    public static final String MXN = "MXN";

    private final String code;

    public CurrencyCode() {
        code = MXN;
    }

    @Override
    public int compareTo(CurrencyCode other) {
        return other == null ? -1 : new CompareToBuilder().append(code, other.code).toComparison();
    }

    @Override
    public String toString() {
        return code;
    }

    @Converter
    public static class DbConverter implements AttributeConverter<CurrencyCode, String> {

        @Override
        public String convertToDatabaseColumn(CurrencyCode code) {
            return code != null ? code.toString() : null;
        }

        @Override
        public CurrencyCode convertToEntityAttribute(String value) {
            return isBlank(value) ? null : new CurrencyCode(value);
        }
    }
}
