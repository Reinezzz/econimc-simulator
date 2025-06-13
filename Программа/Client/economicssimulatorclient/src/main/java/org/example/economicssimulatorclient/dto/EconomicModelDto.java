package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Полное описание экономической модели.
 * Используется при отображении и редактировании модели на клиенте.
 *
 * @param id          идентификатор модели
 * @param modelType   тип модели
 * @param name        название модели
 * @param description текстовое описание
 * @param parameters  список параметров
 * @param results     список результатов
 * @param createdAt   дата создания
 * @param updatedAt   дата последнего обновления
 * @param formula     формула расчёта
 */
public record EconomicModelDto(
        Long id,
        String modelType,
        String name,
        String description,
        List<ModelParameterDto> parameters,
        List<ModelResultDto> results,
        String createdAt,
        String updatedAt,
        String formula
) {}
