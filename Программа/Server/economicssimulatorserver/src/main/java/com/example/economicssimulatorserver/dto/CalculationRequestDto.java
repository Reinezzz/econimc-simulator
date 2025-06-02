package com.example.economicssimulatorserver.dto;

import java.util.List;

public record CalculationRequestDto(
        Long modelId,
        String modelType,
        List<ModelParameterDto> parameters
) {}
