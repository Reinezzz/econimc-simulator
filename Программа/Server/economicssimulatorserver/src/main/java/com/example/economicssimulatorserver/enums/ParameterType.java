package com.example.economicssimulatorserver.enums;

/**
 * Тип параметра математической модели.
 */
public enum ParameterType {
    /**
     * Число с плавающей запятой (double).
     */
    DOUBLE,

    /**
     * Целое число (integer).
     */
    INTEGER,

    /**
     * Строковое значение.
     */
    STRING,

    /**
     * Логический параметр (boolean).
     */
    BOOLEAN,

    /**
     * Массив значений (используется для списков данных).
     */
    ARRAY
}
