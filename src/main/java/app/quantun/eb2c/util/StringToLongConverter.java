package app.quantun.eb2c.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class StringToLongConverter implements AttributeConverter<Long, String> {
    @Override
    public String convertToDatabaseColumn(Long attribute) {
        return attribute != null ? attribute.toString() : null;
    }

    @Override
    public Long convertToEntityAttribute(String dbData) {
        return dbData != null ? Long.valueOf(dbData) : null;
    }
}
