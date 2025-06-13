package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Простая обёртка для сериализации и десериализации JSON с помощью
 * {@link com.fasterxml.jackson.databind.ObjectMapper}, сохранённого в {@link org.example.economicssimulatorclient.config.AppConfig}.
 * Все исключения пробрасываются наружу.
 */
public final class JsonUtil {

    private JsonUtil() {
    }

    /**
     * Сериализует объект в строку JSON.
     *
     * @param obj любой Java-объект
     * @return строка JSON
     * @throws JsonProcessingException если сериализация не удалась
     */
    public static String toJson(Object obj) throws JsonProcessingException {
        return AppConfig.objectMapper.writeValueAsString(obj);
    }

    /**
     * Десериализует строку JSON в указанный класс.
     *
     * @param json JSON-представление объекта
     * @param clz  класс результата
     * @return восстановленный объект
     * @throws JsonProcessingException если разбор не удался
     */
    public static <T> T fromJson(String json, Class<T> clz) throws JsonProcessingException {
        return AppConfig.objectMapper.readValue(json, clz);
    }

}
