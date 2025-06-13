package org.example.economicssimulatorclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Загружает параметры приложения из файла {@code app.properties} в classpath.
 * Полученные значения используются различными сервисами клиента при формировании URL и других настроек.
 * Кроме того, класс предоставляет общий экземпляр {@link ObjectMapper}, настроенный
 * для работы с JSON в приложении.
 */
public class AppConfig {

    private static final Properties props = new Properties();
    public static final ObjectMapper objectMapper = createObjectMapper();

    static {
        try (InputStream in = AppConfig.class.getResourceAsStream("/app.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("config.missing_app_properties"));
            }
        } catch (IOException e) {
            throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("config.load_error"), e);
        }
    }

    /**
     * Возвращает базовый URL сервера для всех REST-запросов.
     * Значение читается из файла настроек по ключу {@code baseUrl} и
     * по умолчанию равно {@code http://localhost:8080}.
     */
    public static String getBaseUrl() {
        return props.getProperty("baseUrl", "http://localhost:8080");
    }

    /**
     * Возвращает тайм-аут HTTP-запроса (мс), указанный в файле настроек
     * под ключом {@code request.timeout}. Если параметр отсутствует,
     * используется значение {@code 10000}.
     */
    public static int getRequestTimeout() {
        return Integer.parseInt(props.getProperty("request.timeout", "10000"));
    }

    /**
     * Создаёт и настраивает {@link ObjectMapper} для работы с JSON.
     * Маппер регистрирует поддержку java.time и отключает вывод дат
     * в виде числовых меток времени.
     */
    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
