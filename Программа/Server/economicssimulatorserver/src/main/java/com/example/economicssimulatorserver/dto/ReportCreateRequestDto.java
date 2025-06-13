package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Запрос на создание отчёта по результатам расчёта модели.
 *
 * @param modelId     идентификатор модели
 * @param modelName   название модели
 * @param name        имя создаваемого отчёта
 * @param language    язык отчёта
 * @param parameters  параметры, использованные при расчёте
 * @param result      итоговый результат модели
 * @param charts      изображения графиков
 * @param llmMessages ответы языковой модели
 * @param parsedResult текстовое представление результата
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
