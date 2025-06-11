package org.example.economicssimulatorclient.dto; // на клиенте пакет аналогичный

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
