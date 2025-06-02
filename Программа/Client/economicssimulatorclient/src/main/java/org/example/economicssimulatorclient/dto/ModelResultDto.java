package org.example.economicssimulatorclient.dto;

public record ModelResultDto(
        Long id,
        Long modelId,          // <--- добавлен modelId!
        String resultType,
        String resultData,
        String calculatedAt
) {}