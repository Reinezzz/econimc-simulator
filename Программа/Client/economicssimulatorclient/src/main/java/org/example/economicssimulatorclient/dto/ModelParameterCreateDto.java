package org.example.economicssimulatorclient.dto;

/**
 * DTO для создания параметра математической модели.
 *
 * @param name имя параметра
 * @param paramType тип параметра
 * @param value значение параметра (строка/JSON)
 * @param description обязательность параметра
 */
public record ModelParameterCreateDto(
        String name,
        String description,
        String paramType,
        String value
) {}

