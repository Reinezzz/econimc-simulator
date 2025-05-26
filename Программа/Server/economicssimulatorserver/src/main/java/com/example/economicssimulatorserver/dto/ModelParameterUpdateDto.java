package com.example.economicssimulatorserver.dto;

import com.example.economicssimulatorserver.enums.ParameterType;

/**
 * DTO для обновления параметра математической модели.
 *
 * @param id идентификатор параметра (нужен для обновления)
 * @param name имя параметра
 * @param paramType тип параметра
 * @param value значение параметра (строка/JSON)
 */
public record ModelParameterUpdateDto(
        Long id,
        String name,
        ParameterType paramType,
        String description,
        String value
) {}
