package org.example.economicssimulatorclient.dto; // на клиенте пакет аналогичный

import java.util.List;

public record ReportCreateRequestDto(
        Long modelId,
        String modelName,
        String name,                 // Имя отчёта (формируется автоматически)
        String language,             // "ru" или "en"
        List<ModelParameterDto> parameters,
        ModelResultDto result,
        List<ReportChartImageDto> charts,
        List<LlmChatResponseDto> llmMessages,
        String parsedResult// Сообщения, выбранные для отчёта
) {}
