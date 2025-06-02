package com.example.economicssimulatorserver.dto;

public record ModelParameterDto(
        Long id,
        Long modelId,        // <---- добавлен обязательный параметр!
        String paramName,
        String paramType,
        String paramValue,
        String displayName,
        String description,
        Integer customOrder
) {}
