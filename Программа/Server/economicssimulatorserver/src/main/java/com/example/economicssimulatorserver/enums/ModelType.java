package com.example.economicssimulatorserver.enums;

/**
 * Типы поддерживаемых математических моделей.
 */
public enum ModelType {
    /**
     * Решение системы линейных уравнений.
     */
    LINEAR_SYSTEM,

    /**
     * Линейная регрессия.
     */
    LINEAR_REGRESSION,

    /**
     * Экспоненциальное сглаживание.
     */
    EXP_SMOOTHING,

    /**
     * Поиск минимума функции.
     */
    FIND_MINIMUM,

    /**
     * Решение квадратного уравнения.
     */
    QUADRATIC_EQUATION
}
