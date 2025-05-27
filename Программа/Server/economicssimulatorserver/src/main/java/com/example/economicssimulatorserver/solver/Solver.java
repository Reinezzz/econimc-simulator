package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.entity.MathModel;
import java.util.Map;

public interface Solver {
    /**
     * Основной метод решения математической модели.
     * @param model Экземпляр модели, включая формулу и параметры.
     * @param parameterValues Значения параметров (id → значение как строка).
     * @return Результат решения (SolverResult).
     * @throws LocalizedException при ошибках вычисления или невозможности решения.
     */
    SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException;

    /**
     * Проверяет, поддерживает ли данный Solver указанную модель.
     * Обычно проверка по типу (model.getType()) и/или структуре формулы.
     * @param model Модель для проверки.
     * @return true если Solver применим, иначе false.
     */
    boolean supports(MathModel model);
}
