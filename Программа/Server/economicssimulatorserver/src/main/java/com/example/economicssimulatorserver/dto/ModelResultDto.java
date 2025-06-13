package com.example.economicssimulatorserver.dto;

/**
 * Данные о результате расчётов модели.
 *
 * @param id          идентификатор результата
 * @param modelId     идентификатор модели
 * @param resultType  тип результата (таблица, график и т.д.)
 * @param resultData  сами данные результата в сериализованном виде
 * @param calculatedAt время, когда расчёт был произведён
 */
public record ModelResultDto(
        Long id,
        Long modelId,
        String resultType,
        String resultData,
        String calculatedAt
) {}

