package com.example.economicssimulatorserver.dto;

public record ModelResultDto(
        Long id,
        Long modelId,          // <--- добавлен modelId!
        String resultType,
        String resultData,
        String calculatedAt
) {}

