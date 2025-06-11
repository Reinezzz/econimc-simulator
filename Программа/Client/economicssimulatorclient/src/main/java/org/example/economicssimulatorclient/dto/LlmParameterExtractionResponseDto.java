package org.example.economicssimulatorclient.dto;

import java.util.List;

public record LlmParameterExtractionResponseDto(
        List<ModelParameterDto> parameters
) {}
