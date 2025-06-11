package com.example.economicssimulatorserver.dto;

public record ModelResultDto(
        Long id,
        Long modelId,
        String resultType,
        String resultData,
        String calculatedAt
) {}

