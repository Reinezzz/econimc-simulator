package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.HttpClientProvider;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Выполняет HTTP‑запросы к /auth/* и хранит access‑token в памяти.
 */
public class AuthService {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_CANCEL_REGISTRATION = "cancel-registration";
    private final URI baseUri = URI.create(AppConfig.BASE_URL + AUTH_PATH + "/");
    private String accessToken;                      // память процесса

    /* =================== Регистрация и верификация =================== */

    public ApiResponse register(RegistrationRequest req) throws IOException, InterruptedException {
        return post("register", req, ApiResponse.class, false);
    }

    public ApiResponse verifyEmail(VerificationRequest req) throws IOException, InterruptedException {
        return post("verify-email", req, ApiResponse.class, false);
    }

    /* =================== Авторизация =================== */

    public LoginResponse login(LoginRequest req) throws IOException, InterruptedException {
        var resp = post("login", req, LoginResponse.class, false);
        this.accessToken = resp.accessToken();
        return resp;
    }

    /* =================== Сброс пароля =================== */

    public ApiResponse resetPasswordRequest(PasswordResetRequest req) throws IOException, InterruptedException {
        return post("password-reset", req, ApiResponse.class, false);
    }

    public ApiResponse resetPasswordConfirm(PasswordResetConfirm req) throws IOException, InterruptedException {
        return post("password-reset/confirm", req, ApiResponse.class, false);
    }

    /* =================== Вспомогательные =================== */

    public boolean isAuthenticated() {
        return accessToken != null;
    }

    public String bearerHeader() {
        return "Bearer " + accessToken;
    }

    private <T> T post(String endpoint, Object body, Class<T> respType, boolean auth) throws IOException, InterruptedException {

        HttpRequest.Builder builder = HttpRequest.newBuilder(baseUri.resolve(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JsonUtil.toJson(body)));

        if (auth) builder.header("Authorization", bearerHeader());

        HttpResponse<String> resp = HttpClientProvider.instance()
                .send(builder.build(), HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return JsonUtil.fromJson(resp.body(), respType);
        }
        // Можно парсить ошибку в ApiResponse, но пока бросим:
        throw new RuntimeException("HTTP " + resp.statusCode() + ": " + resp.body());
    }

    public void cancelRegistration(String email) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUri + AUTH_CANCEL_REGISTRATION))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .build();
        HttpClientProvider.instance().sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

}
