package org.example.economicssimulatorclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Класс для глобальных настроек и параметров приложения.
 * Загружает параметры из файла <b>app.properties</b> и предоставляет утилиты
 * для работы с конфигурацией и сериализацией.
 */
public class AppConfig {

    private static final Properties props = new Properties();
    /** Глобальный ObjectMapper с поддержкой JavaTime. */
    public static final ObjectMapper objectMapper = createObjectMapper();

    /**
     * Статический блок: загружает конфигурацию приложения при старте.
     */
    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException("Не найден app.properties в ресурсах");
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка загрузки app.properties", e);
        }
    }

    /**
     * Получить базовый URL сервера API.
     * @return адрес сервера, по умолчанию {@code http://localhost:8080}
     */
    public static String getBaseUrl() {
        return props.getProperty("baseUrl", "http://81.18.155.15:8080");
    }

    /**
     * Получить таймаут HTTP-запроса в миллисекундах.
     * @return таймаут (по умолчанию 10000 мс)
     */
    public static int getRequestTimeout() {
        return Integer.parseInt(props.getProperty("request.timeout", "10000"));
    }

    /**
     * Создать ObjectMapper с поддержкой JavaTime и без таймстампов для дат.
     * @return настроенный ObjectMapper
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    // Можно добавить геттеры для других параметров
}
