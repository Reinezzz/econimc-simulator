package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;

/**
 * Базовый интерфейс для всех модулей расчёта экономических моделей.
 * <p>
 * Реализации выполняют вычисления для конкретной модели и
 * возвращают {@link com.example.economicssimulatorserver.dto.CalculationResponseDto}
 * с полученными результатами.
 */
public interface EconomicModelSolver {

    /**
     * Выполняет расчёт модели по переданным параметрам.
     *
     * @param request объект с параметрами модели
     * @return {@link com.example.economicssimulatorserver.dto.CalculationResponseDto}
     *         с результатами вычислений и исходными параметрами
     */
    CalculationResponseDto solve(CalculationRequestDto request);

    /**
     * Возвращает уникальный идентификатор модели, которую решает данный солвер.
     *
     * @return строка-ключ для поиска солвера
     */
    String getModelType();
}