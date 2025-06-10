package com.example.economicssimulatorserver.dto;

import java.util.Map;

public record LlmVisualizationDto(
        String chartKey,                  // Уникальный ключ визуализации (например, "supply_demand")
        String chartTitle,                // Человеко-читабельное название графика
        Map<String, Object> chartData// Те данные, которые подаются в визуализатор (например, Series, точки, summary и пр.)
) {}