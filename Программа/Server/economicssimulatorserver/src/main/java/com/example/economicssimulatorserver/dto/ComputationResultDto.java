package com.example.economicssimulatorserver.dto;

import java.time.LocalDateTime;

/**
 * DTO для обмена результатами вычислений с клиентом.
 *
 * @param id идентификатор результата
 * @param mathModelId идентификатор математической модели
 * @param startedAt время начала вычисления
 * @param finishedAt время окончания вычисления
 * @param status статус выполнения вычислений
 * @param resultData сериализованные данные результата (JSON)
 * @param error сообщение об ошибке (если есть)
 */
public record ComputationResultDto(
        Long id,
        Long mathModelId,
        LocalDateTime startedAt,
        LocalDateTime finishedAt,
        String status,
        String resultData,
        String error
) {}
