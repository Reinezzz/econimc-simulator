package com.example.economicssimulatorserver.dto;

import java.util.Map;

/**
 * Описание визуализации, создаваемой по результатам модели.
 *
 * @param chartKey   уникальный ключ графика
 * @param chartTitle заголовок графика
 * @param chartData  данные для построения диаграммы
 */
public record LlmVisualizationDto(
        String chartKey,
        String chartTitle,
        Map<String, Object> chartData
) {}