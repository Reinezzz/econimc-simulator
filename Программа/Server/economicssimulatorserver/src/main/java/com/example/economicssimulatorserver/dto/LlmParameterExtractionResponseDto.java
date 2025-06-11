package com.example.economicssimulatorserver.dto;

import java.util.List;

public record LlmParameterExtractionResponseDto(
        List<ModelParameterDto> parameters
) {}