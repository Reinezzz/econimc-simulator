package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.JsonUtil;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Выполняет HTTP‑запросы к /auth/* и хранит access‑token в памяти.
 */
public class AuthService extends BaseService {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_CANCEL_REGISTRATION = "cancel-registration";
    private static final String AUTH_CANCEL_PASSWORD_RESET = "cancel-password-reset";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + AUTH_PATH + "/");
    private String accessToken;

    /* =================== Регистрация и верификация =================== */

    public ApiResponse register(RegistrationRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "register", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    public ApiResponse verifyEmail(VerificationRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "verify-email", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    /* =================== Авторизация =================== */

    public LoginResponse login(LoginRequest req) throws IOException, InterruptedException {
        try {
            var resp = post(baseUri, "login", req, LoginResponse.class, false, null);
            this.accessToken = resp.accessToken();
            return resp;
        } catch (Exception ex) {
            throw new IllegalArgumentException(extractErrorMessage(ex));
        }
    }

    /* =================== Сброс пароля =================== */

    public ApiResponse resetPasswordRequest(PasswordResetRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "password-reset", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    public ApiResponse resetPasswordConfirm(PasswordResetConfirm req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "password-reset/confirm", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    /* =================== Вспомогательные =================== */

    public boolean isAuthenticated() {
        return accessToken != null;
    }

    public String bearerHeader() {
        return "Bearer " + accessToken;
    }

    public void cancelRegistration(String email) {
        // POST /auth/cancel-registration с email в теле
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_REGISTRATION))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    public void cancelPasswordReset(String email) {
        // POST /auth/cancel-password-reset с email в теле
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_PASSWORD_RESET))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    private String extractErrorMessage(Exception ex) {
        // Попытаться найти JSON-ответ с полем "message"
        String msg = ex.getMessage();
        if (msg != null && msg.contains("{") && msg.contains("message")) {
            try {
                String json = msg.substring(msg.indexOf("{"));
                ApiResponse resp = JsonUtil.fromJson(json, ApiResponse.class);
                if (resp != null && resp.message() != null) {
                    return resp.message();
                }
            } catch (Exception ignored) {
            }
        }
        // Вернуть исходное сообщение, если не получилось парсить JSON
        return msg;
    }
}
