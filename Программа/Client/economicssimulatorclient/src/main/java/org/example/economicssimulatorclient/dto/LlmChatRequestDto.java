package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Запрос к языковой модели для генерации ответа.
 * Используется при общении пользователя с ИИ‑помощником.
 *
 * @param modelId       идентификатор экономической модели
 * @param userMessage   текст сообщения пользователя
 * @param parameters    параметры модели
 * @param visualizations данные для построения графиков
 * @param result        расчётный результат модели
 */
public record LlmChatRequestDto(
        Long modelId,
        String userMessage,
        List<ModelParameterDto> parameters,
        List<LlmVisualizationDto> visualizations,
        ModelResultDto result
) {}
