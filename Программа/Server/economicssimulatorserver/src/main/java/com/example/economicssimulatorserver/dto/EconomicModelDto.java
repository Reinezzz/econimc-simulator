package com.example.economicssimulatorserver.dto;

import java.util.List;

public record EconomicModelDto(
        Long id,
        String modelType,
        String name,
        String description,
        List<ModelParameterDto> parameters,
        List<ModelResultDto> results,
        String createdAt,
        String updatedAt,
        String formula
) {}

