package org.example.economicssimulatorclient.dto;

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
        String description,
        String paramType,
        String value
) {}