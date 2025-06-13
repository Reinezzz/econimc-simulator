package org.example.economicssimulatorclient.dto;

import java.util.List;

/**
 * Запрос на расчёт экономической модели.
 * Передаётся на сервер при запуске вычислений.
 *
 * @param modelId     идентификатор модели
 * @param modelType   тип модели
 * @param parameters  перечень параметров модели
 */
public record CalculationRequestDto(
        Long modelId,
        String modelType,
        List<ModelParameterDto> parameters
) {}
