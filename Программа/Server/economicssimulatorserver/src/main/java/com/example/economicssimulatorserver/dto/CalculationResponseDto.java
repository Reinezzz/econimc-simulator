package com.example.economicssimulatorserver.dto;

import java.util.List;

public record CalculationResponseDto(
        ModelResultDto result,
        List<ModelParameterDto> updatedParameters
) {}
