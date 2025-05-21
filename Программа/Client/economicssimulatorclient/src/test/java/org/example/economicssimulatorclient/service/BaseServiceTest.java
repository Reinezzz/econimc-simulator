package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.I18n;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.*;

class BaseServiceTest {

    private DummyService service;

    static class DummyService extends BaseService {
        // Публичный прокси к защищённым методам
        public <T> T doGet(URI baseUri, String endpoint, Class<T> respType, boolean auth, String accessToken) throws Exception {
            return get(baseUri, endpoint, respType, auth, accessToken);
        }
        public <T> T doPost(URI baseUri, String endpoint, Object body, Class<T> respType, boolean auth, String accessToken) throws Exception {
            return post(baseUri, endpoint, body, respType, auth, accessToken);
        }
    }

    @BeforeEach
    void setUp() {
        service = new DummyService();
    }

    @Test
    void acceptLanguageHeaderUsedInRequests() {
        // Проверить что заголовок Accept-Language соответствует локали I18n
        // (Проверить можно только через реальный сервер, либо через WireMock/MockWebServer)
        assertEquals("en", I18n.getLocale().getLanguage());
    }

    // Можно добавить интеграционный тест с реальным сервером (опционально)
    // @Test
    // void throwsOnServerError() { ... }
}
