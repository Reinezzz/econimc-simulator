package org.example.economicssimulatorclient.util;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Ленивый singleton-провайдер для {@link HttpClient} с дефолтным таймаутом.
 * Используется всеми сервисами клиента для HTTP-запросов.
 */
public final class HttpClientProvider {

    /**
     * Синглтон-инстанс {@link HttpClient}.
     */
    private static volatile HttpClient client;

    /**
     * Приватный конструктор для запрета создания экземпляров.
     */
    private HttpClientProvider() {}

    /**
     * Возвращает singleton-инстанс {@link HttpClient} (ленивая инициализация, потокобезопасно).
     * @return {@link HttpClient}
     */
    public static HttpClient instance() {
        if (client == null) {
            synchronized (HttpClientProvider.class) {
                if (client == null) {
                    client = HttpClient.newBuilder()
                            .connectTimeout(Duration.ofSeconds(10))
                            .build();
                }
            }
        }
        return client;
    }
}
