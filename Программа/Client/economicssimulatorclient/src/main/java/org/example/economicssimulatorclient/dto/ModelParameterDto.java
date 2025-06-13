package org.example.economicssimulatorclient.dto;

/**
 * Параметр экономической модели.
 * Используется при хранении и передаче конфигурации модели.
 *
 * @param id          идентификатор параметра
 * @param modelId     идентификатор модели
 * @param paramName   имя параметра
 * @param paramType   тип значения
 * @param paramValue  текущее значение
 * @param displayName отображаемое название
 * @param description описание параметра
 * @param customOrder порядок отображения
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
