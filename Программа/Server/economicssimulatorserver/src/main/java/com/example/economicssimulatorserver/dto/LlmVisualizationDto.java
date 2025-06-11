package com.example.economicssimulatorserver.dto;

import java.util.Map;

public record LlmVisualizationDto(
        String chartKey,
        String chartTitle,
        Map<String, Object> chartData
) {}