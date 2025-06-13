package com.example.economicssimulatorserver.dto;

import java.util.List;

/**
 * Запрос на выполнение расчёта экономической модели.
 *
 * @param modelId    идентификатор выбранной модели
 * @param modelType  тип модели, например "macroeconomic"
 * @param parameters список параметров, используемых в расчёте
 */
public record CalculationRequestDto(
        Long modelId,
        String modelType,
        List<ModelParameterDto> parameters
) {}
