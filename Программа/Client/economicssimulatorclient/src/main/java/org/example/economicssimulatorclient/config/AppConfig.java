package org.example.economicssimulatorclient.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

    public static String getBaseUrl() {
        return props.getProperty("baseUrl", "http://localhost:8080");
    }

    public static int getRequestTimeout() {
        return Integer.parseInt(props.getProperty("request.timeout", "10000"));
    }

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
}
