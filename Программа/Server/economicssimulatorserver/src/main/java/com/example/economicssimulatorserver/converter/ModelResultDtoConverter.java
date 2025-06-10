package com.example.economicssimulatorserver.converter;

import com.example.economicssimulatorserver.dto.ModelResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class ModelResultDtoConverter implements AttributeConverter<ModelResultDto, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ModelResultDto attribute) {
        try {
            return attribute == null ? "{}" : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert ModelResultDto to JSON", e);
        }
    }

    @Override
    public ModelResultDto convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return null;
            return mapper.readValue(dbData, ModelResultDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON to ModelResultDto", e);
        }
    }
}
