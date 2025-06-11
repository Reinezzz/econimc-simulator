package org.example.economicssimulatorclient.dto;

public record ModelResultDto(
        Long id,
        Long modelId,
        String resultType,
        String resultData,
        String calculatedAt
) {}