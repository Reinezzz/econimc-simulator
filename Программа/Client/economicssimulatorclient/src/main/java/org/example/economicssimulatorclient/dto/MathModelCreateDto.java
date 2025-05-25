package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * DTO для создания новой математической модели.
 *
 * @param name имя модели
 * @param formula тип математической модели
 * @param type тип модели
 * @param parameters параметры создаваемой модели
 */
public record MathModelCreateDto(
        String name,
        String formula,
        String type,
        List<ModelParameterCreateDto> parameters
) {}

