package org.example.economicssimulatorclient.dto;

public record ModelParameterDto(
        Long id,
        Long modelId,
        String paramName,
        String paramType,
        String paramValue,
        String displayName,
        String description,
        Integer customOrder
) {}
