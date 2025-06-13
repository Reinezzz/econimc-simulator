package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Ответ на запрос расчёта экономической модели.
 *
 * @param result            итоговые результаты вычислений
 * @param updatedParameters обновлённые значения параметров модели
 */
public record CalculationResponseDto(
        ModelResultDto result,
        List<ModelParameterDto> updatedParameters
) {}
