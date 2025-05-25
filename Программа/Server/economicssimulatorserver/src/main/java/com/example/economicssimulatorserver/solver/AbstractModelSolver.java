package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.exception.LocalizedException;

/**
 * Абстрактный базовый класс для всех вычислителей математических моделей.
 */
public abstract class AbstractModelSolver {

    /**
     * Запускает вычисление по модели.
     *
     * @param model математическая модель (с параметрами)
     * @return результат вычислений
     * @throws LocalizedException если данные некорректны или вычисление невозможно
     */
    public abstract SolverResult solve(MathModel model) throws LocalizedException;
}
