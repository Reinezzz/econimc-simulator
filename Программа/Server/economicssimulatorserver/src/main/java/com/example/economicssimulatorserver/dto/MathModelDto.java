package com.example.economicssimulatorserver.dto;

import com.example.economicssimulatorserver.enums.ModelType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO для обмена информацией о математической модели с клиентом.
 *
 * @param id идентификатор модели
 * @param formula формула модели
 * @param name название модели
 * @param type тип модели
 * @param parameters параметры
 * @param userId идентификатор пользователя
 */
public record MathModelDto(
        Long id,
        String name,
        String formula,
        String type,
        List<ModelParameterDto> parameters,
        Long userId
) {}
