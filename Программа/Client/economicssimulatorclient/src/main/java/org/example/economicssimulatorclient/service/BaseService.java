package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.util.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class BaseService {

    protected HttpClient httpClient = HttpClientProvider.instance();

    protected <T> T post(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        String jsonBody;
        try {
            jsonBody = JsonUtil.toJson(body);
        } catch (Exception e) {
            throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.serialization") + e.getMessage(), e);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.http") + resp.statusCode() + ": " + resp.body());
    }

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

        throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.http") + resp.statusCode() + ": " + resp.body());
    }

    protected <T> T put(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {

        String jsonBody;
        try {
            jsonBody = JsonUtil.toJson(body);
        } catch (Exception e) {
            throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.serialization") + e.getMessage(), e);
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));

        if (auth && accessToken != null)
            builder.header("Authorization", "Bearer " + accessToken);

        HttpResponse<String> resp = httpClient
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }

        throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.http") + resp.statusCode() + ": " + resp.body());
    }

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
            throw new RuntimeException(org.example.economicssimulatorclient.util.I18n.t("error.http") + resp.statusCode() + ": " + resp.body());
        }
    }

}
