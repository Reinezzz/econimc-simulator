package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Полное описание экономической модели, доступной пользователю.
 *
 * @param id          идентификатор модели
 * @param modelType   тип модели
 * @param name        название модели
 * @param description текстовое описание
 * @param parameters  список параметров модели
 * @param results     результаты последних вычислений
 * @param createdAt   дата создания
 * @param updatedAt   дата последнего обновления
 * @param formula     формула модели в удобном представлении
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

