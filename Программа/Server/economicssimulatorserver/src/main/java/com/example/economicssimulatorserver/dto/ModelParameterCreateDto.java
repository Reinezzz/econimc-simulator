package com.example.economicssimulatorserver.dto;

import com.example.economicssimulatorserver.enums.ParameterType;

/**
 * DTO для создания параметра математической модели.
 *
 * @param name имя параметра
 * @param paramType тип параметра
 * @param value значение параметра (строка/JSON)
 * @param required обязательность параметра
 */
public record ModelParameterCreateDto(
        String name,
        ParameterType paramType,
        String description,
        String value,
        boolean required
) {}
