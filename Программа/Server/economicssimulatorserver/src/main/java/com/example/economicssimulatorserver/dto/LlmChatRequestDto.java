package com.example.economicssimulatorserver.dto;

import java.util.List;

public record LlmChatRequestDto(
        Long modelId,
        String userMessage,
        List<ModelParameterDto> parameters,
        List<LlmVisualizationDto> visualizations,
        ModelResultDto result
) {}
