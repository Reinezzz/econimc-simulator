package org.example.economicssimulatorclient.util;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class HttpClientProviderTest {

    @Test
    void instanceReturnsSingleton() {
        HttpClient client1 = HttpClientProvider.instance();
        HttpClient client2 = HttpClientProvider.instance();
        assertSame(client1, client2, "instance() должен возвращать singleton");
    }

    @Test
    void clientHasDefaultTimeout() {
        HttpClient client = HttpClientProvider.instance();
        // Не у всех HttpClient можно получить таймаут напрямую, но если конфигурируется — тестируй так:
        assertEquals(Duration.ofSeconds(10), client.connectTimeout().orElseThrow());
    }
}
