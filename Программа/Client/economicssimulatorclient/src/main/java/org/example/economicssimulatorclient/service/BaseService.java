package org.example.economicssimulatorclient.service;

import javafx.application.Platform;
import org.example.economicssimulatorclient.util.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Базовый сервис для выполнения HTTP-запросов к серверу API.
 * Реализует общую логику GET и POST с учетом авторизации и локализации.
 */
public abstract class BaseService {

    /** HTTP-клиент для отправки запросов. */
    protected final HttpClient httpClient = HttpClientProvider.instance();

    /**
     * Выполнить POST-запрос и вернуть десериализованный ответ.
     *
     * @param baseUri   базовый URI сервера
     * @param endpoint  относительный путь (например, "login")
     * @param body      тело запроса (DTO)
     * @param respType  класс типа ответа
     * @param auth      добавлять ли JWT-токен в заголовок Authorization
     * @param accessToken JWT-токен (если требуется)
     * @param <T>       тип ответа
     * @return десериализованный ответ типа T
     * @throws IOException если ошибка при отправке запроса
     * @throws InterruptedException если поток прерван
     */
    protected <T> T post(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

    /**
     * Выполнить GET-запрос и вернуть десериализованный ответ.
     *
     * @param baseUri   базовый URI сервера
     * @param endpoint  относительный путь
     * @param respType  класс типа ответа
     * @param auth      добавлять ли JWT-токен в заголовок Authorization
     * @param accessToken JWT-токен (если требуется)
     * @param <T>       тип ответа
     * @return десериализованный ответ типа T
     * @throws IOException если ошибка при отправке запроса
     * @throws InterruptedException если поток прерван
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
                .header("Accept-Language", I18n.getLocale().getLanguage())
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

    /**
     * Выполнить PUT-запрос и вернуть десериализованный ответ.
     *
     * @param baseUri   базовый URI сервера
     * @param endpoint  относительный путь (например, "models/1")
     * @param body      тело запроса (DTO)
     * @param respType  класс типа ответа
     * @param auth      добавлять ли JWT-токен в заголовок Authorization
     * @param accessToken JWT-токен (если требуется)
     * @param <T>       тип ответа
     * @return десериализованный ответ типа T
     * @throws IOException если ошибка при отправке запроса
     * @throws InterruptedException если поток прерван
     */
    protected <T> T put(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .PUT(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

    /**
     * Выполнить DELETE-запрос.
     *
     * @param baseUri   базовый URI сервера
     * @param endpoint  относительный путь (например, "models/1")
     * @param auth      добавлять ли JWT-токен в заголовок Authorization
     * @param accessToken JWT-токен (если требуется)
     * @throws IOException если ошибка при отправке запроса
     * @throws InterruptedException если поток прерван
     */
    protected void delete(
            URI baseUri,
            String endpoint,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .DELETE();

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (!(resp.statusCode() >= 200 && resp.statusCode() < 300)) {
            throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
        }
    }

}
