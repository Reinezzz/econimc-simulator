package org.example.economicssimulatorclient.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.example.economicssimulatorclient.dto.ModelResultDto;

import java.util.*;

/**
 * Утилита для преобразования JSON-результатов модели в структуры,
 * пригодные для построения графиков.
 * Данные берутся из {@link org.example.economicssimulatorclient.dto.ModelResultDto#resultData()},
 * а для парсинга используется {@link com.fasterxml.jackson.databind.ObjectMapper}.
 */
public class ChartDataConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Разбирает сырой JSON и превращает его в карту данных по ключу графика.
     *
     * @param result результат модели с полем JSON {@code resultData}
     * @return карта вида {@code имяГрафика -> (параметр -> значение)}
     */
    public static Map<String, Map<String, Object>> parseRawChartData(ModelResultDto result) {
        if (result == null || result.resultData() == null) return Collections.emptyMap();
        try {
            JsonNode root = objectMapper.readTree(result.resultData());

            if (root.isObject()) {
                Map<String, Map<String, Object>> out = new LinkedHashMap<>();
                Iterator<String> keys = root.fieldNames();
                while (keys.hasNext()) {
                    String key = keys.next();
                    JsonNode node = root.get(key);
                    Object value;
                    if (node.isObject() || node.isArray()) {
                        value = objectMapper.convertValue(node, new TypeReference<Object>() {
                        });
                    } else if (node.isNumber()) {
                        value = node.numberValue();
                    } else if (node.isTextual()) {
                        value = node.textValue();
                    } else {
                        value = node.toString();
                    }
                    if (value instanceof Map) {
                        out.put(key, (Map<String, Object>) value);
                    } else {
                        out.put(key, Map.of("value", value));
                    }
                }
                return out;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }

    /**
     * Извлекает блок {@code equilibrium} или {@code optimum} из результата модели.
     *
     * @param result результат работы модели
     * @return карта параметров равновесия
     */
    public static Map<String, Double> parseEquilibrium(ModelResultDto result) {
        Map<String, Double> equilibrium = new HashMap<>();
        if (result == null || result.resultData() == null) return equilibrium;

        try {
            JsonNode root = objectMapper.readTree(result.resultData());
            if (root.has("equilibrium")) {
                JsonNode eq = root.get("equilibrium");
                eq.fieldNames().forEachRemaining(f -> equilibrium.put(f, eq.get(f).asDouble()));
            } else if (root.has("optimum")) {
                JsonNode opt = root.get("optimum");
                opt.fieldNames().forEachRemaining(f -> equilibrium.put(f, opt.get(f).asDouble()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return equilibrium;
    }
}
