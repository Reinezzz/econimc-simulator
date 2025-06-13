package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Запрос к модели ИИ для генерации ответа в чате.
 *
 * @param modelId       идентификатор экономической модели
 * @param userMessage   исходное сообщение пользователя
 * @param parameters    параметры модели, участвующие в диалоге
 * @param visualizations данные для построения графиков
 * @param result        текущий расчётный результат модели
 */
public record LlmChatRequestDto(
        Long modelId,
        String userMessage,
        List<ModelParameterDto> parameters,
        List<LlmVisualizationDto> visualizations,
        ModelResultDto result
) {}
