package org.example.economicssimulatorclient.dto;

import java.util.List;

public record CalculationRequestDto(
        Long modelId,
        String modelType,
        List<ModelParameterDto> parameters
) {}
