package org.example.economicssimulatorclient.dto;

import java.util.Map;

/**
 * Данные для визуализации, сгенерированной ИИ.
 *
 * @param chartKey   ключ графика
 * @param chartTitle заголовок графика
 * @param chartData  набор данных для построения
 */
public record LlmVisualizationDto(
        String chartKey,
        String chartTitle,
        Map<String, Object> chartData
) {}