package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ParameterTypeConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

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

    public static String toString(Object value, String type) {
        if (value == null) return null;
        return switch (type.toLowerCase()) {
            case "json"     -> writeJson(value);
            default         -> value.toString();
        };
    }

    private static JsonNode parseJson(String value) {
        try {
            return objectMapper.readTree(value);
        } catch (Exception e) {
            throw new LocalizedException("error.json_parse", value);
        }
    }

    private static String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new LocalizedException("error.json_write", value);
        }
    }
}
