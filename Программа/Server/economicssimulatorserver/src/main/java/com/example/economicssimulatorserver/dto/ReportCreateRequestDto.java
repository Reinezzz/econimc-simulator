package com.example.economicssimulatorserver.dto;

import java.util.List;

public record ReportCreateRequestDto(
        Long modelId,
        String modelName,
        String name,
        String language,
        List<ModelParameterDto> parameters,
        ModelResultDto result,
        List<ReportChartImageDto> charts,
        List<LlmChatResponseDto> llmMessages,
        String parsedResult
) {}
