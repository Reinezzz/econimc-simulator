package org.example.economicssimulatorclient.dto;

public record LlmParameterExtractionRequestDto(
        Long modelId,
        Long documentId
) {}
