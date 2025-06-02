package org.example.economicssimulatorclient.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AppConfigTest {

    @Test
    void testGetBaseUrl() {
        String url = AppConfig.getBaseUrl();
        assertNotNull(url);
        assertFalse(url.isBlank());
    }

    @Test
    void testGetRequestTimeout() {
        int timeout = AppConfig.getRequestTimeout();
        assertTrue(timeout > 0);
    }

    @Test
    void objectMapperNotNull() {
        assertNotNull(AppConfig.objectMapper);
    }

    @Test
    void objectMapperConfig() {
        assertFalse(AppConfig.objectMapper.getSerializationConfig().isEnabled(
                com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
        ));
    }
}
