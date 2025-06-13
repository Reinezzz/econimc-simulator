package org.example.economicssimulatorclient.dto;

/**
 * Результат расчёта экономической модели.
 *
 * @param id           идентификатор результата
 * @param modelId      идентификатор модели
 * @param resultType   тип результата
 * @param resultData   данные результата
 * @param calculatedAt дата и время расчёта
 */
public record ModelResultDto(
        Long id,
        Long modelId,
        String resultType,
        String resultData,
        String calculatedAt
) {}