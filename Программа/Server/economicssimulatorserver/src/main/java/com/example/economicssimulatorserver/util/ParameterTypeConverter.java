package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Утилита для преобразования строковых параметров в типы и обратно.
 */
public class ParameterTypeConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Преобразует строку в объект указанного типа.
     *
     * @param value исходное строковое значение
     * @param type  тип целевого объекта (int, double, boolean, json и т. д.)
     * @return объект соответствующего типа или {@code null}, если {@code value} равно {@code null}
     * @throws LocalizedException при ошибке разбора JSON
     */
    public static Object fromString(String value, String type) {
        if (value == null) return null;
        return switch (type.toLowerCase()) {
            case "int", "integer"    -> Integer.valueOf(value);
            case "double", "float"   -> Double.valueOf(value);
            case "boolean"           -> Boolean.valueOf(value);
            case "json"              -> parseJson(value);
            default                  -> value;
        };
    }

    /**
     * Преобразует объект в строку в зависимости от указанного типа.
     *
     * @param value объект для преобразования
     * @param type  строковое имя типа (например, {@code "json"})
     * @return строковое представление объекта или {@code null}, если {@code value} равно {@code null}
     * @throws LocalizedException при ошибке сериализации JSON
     */
    public static String toString(Object value, String type) {
        if (value == null) return null;
        return switch (type.toLowerCase()) {
            case "json"     -> writeJson(value);
            default         -> value.toString();
        };
    }

    /**
     * Преобразует строку JSON в {@link JsonNode}.
     *
     * @param value строка JSON
     * @return дерево JSON
     * @throws LocalizedException если входная строка не является корректным JSON
     */
    private static JsonNode parseJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (Exception e) {
            throw new LocalizedException("error.json_parse", value);
        }
    }

    /**
     * Сериализует объект в строку JSON.
     *
     * @param value объект для сериализации
     * @return строка JSON
     * @throws LocalizedException если объект не удалось записать в JSON
     */
    private static String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new LocalizedException("error.json_write", value);
        }
    }
}
