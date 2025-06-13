package com.example.economicssimulatorserver.dto;

/**
 * Описание входного параметра экономической модели.
 *
 * @param id          идентификатор параметра
 * @param modelId     идентификатор модели
 * @param paramName   системное название параметра
 * @param paramType   тип значения (например, число или строка)
 * @param paramValue  текущее значение параметра
 * @param displayName отображаемое пользователю имя
 * @param description поясняющее описание
 * @param customOrder порядок отображения в списке
 */
public record ModelParameterDto(
        Long id,
        Long modelId,
        String paramName,
        String paramType,
        String paramValue,
        String displayName,
        String description,
        Integer customOrder
) {}
