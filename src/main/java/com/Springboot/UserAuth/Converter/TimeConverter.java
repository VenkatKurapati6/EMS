package com.Springboot.UserAuth.Converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.LocalTime;

@Converter(autoApply = true)
public class TimeConverter implements AttributeConverter<LocalTime, String> {

    @Override
    public String convertToDatabaseColumn(LocalTime time) {
        return time != null ? String.format("%02d:%02d", time.getHour(), time.getMinute()) : null;
    }

    @Override
    public LocalTime convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalTime.parse(dbData) : null;
    }
}
