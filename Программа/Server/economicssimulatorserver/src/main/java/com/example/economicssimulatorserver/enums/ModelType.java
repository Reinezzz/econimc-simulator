package com.example.economicssimulatorserver.enums;

/**
 * Типы поддерживаемых математических моделей.
 */
public enum ModelType {
    /**
     * Линейное уравнение.
     */
    ALGEBRAIC_EQUATION,


    /**
     * Оптимизация.
     */
    OPTIMIZATION,

    /**
     * Множественная линейная регрессия.
     */
    REGRESSION,

    /**
     * Система уравнений.
     */
    SYSTEM_OF_EQUATIONS
}
