package org.example.economicssimulatorclient.util;

import java.net.http.HttpClient;
import java.time.Duration;

/**
 * Поставщик общего HTTP клиента.
 * Клиент лениво создаётся при первом обращении
 * и настраивается на таймаут подключения в 10 секунд.
 * Применяется для всех HTTP-запросов в клиентском приложении.
 */
public final class HttpClientProvider {

    private static volatile HttpClient client;

    private HttpClientProvider() {
    }

    /**
     * Возвращает общий HTTP-клиент, создавая его при первом вызове.
     * Метод потокобезопасен благодаря двойной проверке и синхронизации.
     *
     * @return настроенный экземпляр {@link HttpClient}
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
