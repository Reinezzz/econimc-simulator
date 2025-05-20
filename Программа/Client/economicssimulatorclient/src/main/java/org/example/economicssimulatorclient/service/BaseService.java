package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.HttpClientProvider;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseService {

    protected final HttpClient httpClient = HttpClientProvider.instance();

    /**
     * Выполнить POST-запрос, вернуть десериализованный ответ.
     *
     * @param endpoint относительный путь (например, "login")
     * @param body     тело запроса (DTO)
     * @param respType класс ответа
     * @param auth     добавлять ли JWT
     */
    protected <T> T post(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken // или null
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        // Осмысленная обработка ошибок (например, всегда ApiResponse)
        // Можно кастомизировать под свои форматы!
        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

    /**
     * Выполнить GET-запрос и вернуть десериализованный ответ.
     */
    protected <T> T get(
            URI baseUri,
            String endpoint,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Accept", "application/json")
                .GET();

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

}
