package org.example.economicssimulatorclient.dto;

import java.util.List;

public record CalculationResponseDto(
        ModelResultDto result,
        List<ModelParameterDto> updatedParameters // если модель меняет параметры при расчете
) {}
