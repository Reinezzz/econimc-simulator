package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ModelParameterDto;

public class ParameterValidator {

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

    public static boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

}
