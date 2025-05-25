package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * DTO для обновления существующей математической модели.
 * @param id идентификатор модели
 * @param name новое имя модели
 * @param formula новый тип модели
 * @param type Тип модели
 * @param parameters обновленный список параметров
 */
public record MathModelUpdateDto(
        Long id,
        String name,
        String formula,
        String type,
        List<ModelParameterUpdateDto> parameters
) {}

