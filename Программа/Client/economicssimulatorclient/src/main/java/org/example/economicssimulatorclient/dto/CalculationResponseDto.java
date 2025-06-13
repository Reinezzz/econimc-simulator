package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Ответ сервера на запрос расчёта модели.
 * Содержит результат вычислений и список обновлённых параметров.
 *
 * @param result            рассчитанные данные
 * @param updatedParameters параметры, значения которых были изменены
 */
public record CalculationResponseDto(
        ModelResultDto result,
        List<ModelParameterDto> updatedParameters
) {}
