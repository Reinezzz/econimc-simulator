package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ModelParameterDto;

/**
 * Проверяет корректность параметров модели в зависимости от заданного типа.
 * Поддерживает числовые, логические и JSON-значения,
 * а также простую проверку на непустоту.
 */
public class ParameterValidator {

    /**
     * Проверяет значение параметра модели согласно его типу.
     * Поддерживаются числовые, логические и JSON-значения.
     *
     * @param param параметр модели
     * @return {@code true}, если значение корректно
     */
    public static boolean isValid(ModelParameterDto param) {
        if (param == null || param.paramValue() == null) return false;
        String type = param.paramType().toLowerCase();
        String value = param.paramValue();

        try {
            switch (type) {
                case "int", "integer" -> Integer.parseInt(value);
                case "double", "float" -> Double.parseDouble(value);
                case "boolean" -> {
                    if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false"))
                        return false;
                }
                case "json" -> {
                    if (!value.trim().startsWith("{") && !value.trim().startsWith("[")) return false;
                }
                default -> {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Простая проверка строки на непустоту.
     *
     * @param value текстовое значение
     * @return {@code true}, если строка не пуста
     */
    public static boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
