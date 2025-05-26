package org.example.economicssimulatorclient.dto;

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
        String description,
        String paramType,
        String value
) {}
