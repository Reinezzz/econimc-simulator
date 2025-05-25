package com.example.economicssimulatorserver.enums;

/**
 * Статус выполнения вычислений модели.
 */
public enum ComputationStatus {
    /**
     * Задача в очереди.
     */
    PENDING,

    /**
     * Вычисление в процессе.
     */
    RUNNING,

    /**
     * Вычисление успешно завершено.
     */
    SUCCESS,

    /**
     * Произошла ошибка при вычислении.
     */
    FAILED
}
