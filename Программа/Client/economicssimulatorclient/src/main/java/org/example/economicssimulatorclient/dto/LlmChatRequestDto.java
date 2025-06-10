package org.example.economicssimulatorclient.dto;

import java.util.List;

public record LlmChatRequestDto(
        Long modelId,
        String userMessage,
        List<ModelParameterDto> parameters,          // Используем твой ModelParameterDto
        List<LlmVisualizationDto> visualizations,    // Для экрана результата, на экране view может быть null
        ModelResultDto result                        // Для экрана результата, на экране view null
) {}
