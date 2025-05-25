package com.example.economicssimulatorserver.dto;

import com.example.economicssimulatorserver.enums.ModelType;

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
