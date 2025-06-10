package com.example.economicssimulatorserver.dto;

public record LlmParameterExtractionRequestDto(
        Long modelId,
        Long documentId
) {}
