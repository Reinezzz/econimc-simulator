package org.example.economicssimulatorclient.util;

import java.net.http.HttpClient;
import java.time.Duration;

/** Ленивый singleton для HttpClient (HTTP/1.1 + HTTP/2). */
public final class HttpClientProvider {

    private static volatile HttpClient client;

    private HttpClientProvider() {}

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
