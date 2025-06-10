package com.example.economicssimulatorserver.converter;

import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class ModelParameterDtoListConverter implements AttributeConverter<List<ModelParameterDto>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<ModelParameterDto> attribute) {
        try {
            return attribute == null ? "[]" : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert ModelParameterDto list to JSON", e);
        }
    }

    @Override
    public List<ModelParameterDto> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return List.of();
            return mapper.readValue(dbData, new TypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON to ModelParameterDto list", e);
        }
    }
}
