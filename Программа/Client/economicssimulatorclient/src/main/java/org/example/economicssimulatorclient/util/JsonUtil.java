package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.config.AppConfig;
import com.fasterxml.jackson.core.JsonProcessingException;

public final class JsonUtil {

    private JsonUtil() {}

    public static String toJson(Object obj) throws JsonProcessingException {
        return AppConfig.objectMapper.writeValueAsString(obj);
    }

    public static <T> T fromJson(String json, Class<T> clz) throws JsonProcessingException {
        return AppConfig.objectMapper.readValue(json, clz);
    }
}
