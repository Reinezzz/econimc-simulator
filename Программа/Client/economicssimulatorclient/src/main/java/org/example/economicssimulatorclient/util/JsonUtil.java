package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Утилита для сериализации и десериализации объектов в/из JSON.
 */
public final class JsonUtil {

    /**
     * Приватный конструктор для запрета создания экземпляров.
     */
    private JsonUtil() {}

    /**
     * Сериализует объект в строку JSON.
     * @param obj объект для сериализации
     * @return строка JSON
     * @throws JsonProcessingException при ошибке сериализации
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return AppConfig.objectMapper.writeValueAsString(obj);
    }

    /**
     * Десериализует строку JSON в объект заданного класса.
     * @param json строка JSON
     * @param clz класс результата
     * @param <T> тип результата
     * @return объект типа T
     * @throws JsonProcessingException при ошибке десериализации
     */
    public static <T> T fromJson(String json, Class<T> clz) throws JsonProcessingException {
        return AppConfig.objectMapper.readValue(json, clz);
    }
}
