package org.example.economicssimulatorclient.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Глобальная конфигурация клиента: базовый URL и настроенный ObjectMapper.
 */
public final class AppConfig {

    /** URL сервера (без завершающего /).  Измените при необходимости. */
    public static final String BASE_URL = "http://localhost:8080";

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())            // поддержка Java Time
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private AppConfig() {}   // utility – нет инстансов

    public static ObjectMapper mapper() {
        return MAPPER;
    }
}
