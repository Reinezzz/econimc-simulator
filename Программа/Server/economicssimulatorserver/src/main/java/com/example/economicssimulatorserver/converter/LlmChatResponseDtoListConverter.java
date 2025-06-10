package com.example.economicssimulatorserver.converter;

import com.example.economicssimulatorserver.dto.LlmChatResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter(autoApply = false)
public class LlmChatResponseDtoListConverter implements AttributeConverter<List<LlmChatResponseDto>, String> {
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<LlmChatResponseDto> attribute) {
        try {
            // Гарантированно отдаём json-массив даже если null
            return (attribute == null || attribute.isEmpty()) ? "[]" : mapper.writeValueAsString(attribute);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert LlmChatResponseDto list to JSON", e);
        }
    }

    @Override
    public List<LlmChatResponseDto> convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.isEmpty()) return List.of();
            return mapper.readValue(dbData, new TypeReference<List<LlmChatResponseDto>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to convert JSON to LlmChatResponseDto list", e);
        }
    }
}
