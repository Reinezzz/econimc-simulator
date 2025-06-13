package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Запрос на создание отчёта по выполненным расчётам.
 *
 * @param modelId      идентификатор модели
 * @param modelName    название модели
 * @param name         название отчёта
 * @param language     язык отчёта
 * @param parameters   параметры модели
 * @param result       полученный результат
 * @param charts       изображения графиков
 * @param llmMessages  сообщения, сгенерированные ИИ
 * @param parsedResult текст, подготовленный для отчёта
 */
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
