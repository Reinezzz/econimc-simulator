package com.example.economicssimulatorserver.dto;

import com.example.economicssimulatorserver.enums.ParameterType;

/**
 * DTO для обмена параметрами модели с клиентом.
 *
 * @param id идентификатор параметра
 * @param mathModelId идентификатор математической модели
 * @param name имя параметра
 * @param paramType тип параметра
 * @param value значение параметра (строка/JSON)
 */
public record ModelParameterDto(
        Long id,
        Long mathModelId,
        String name,
        ParameterType paramType,
        String description,
        String value
) {}
