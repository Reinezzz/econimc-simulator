package org.example.economicssimulatorclient.service;

import lombok.Getter;
import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.JsonUtil;
import org.example.economicssimulatorclient.util.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Выполняет HTTP‑запросы к /auth/* и хранит access‑token в памяти.
 * Используется для авторизации, регистрации, сброса пароля и работы с email-подтверждениями.
 */
public class AuthService extends BaseService {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_CANCEL_REGISTRATION = "cancel-registration";
    private static final String AUTH_CANCEL_PASSWORD_RESET = "cancel-password-reset";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + AUTH_PATH + "/");

    private static final AuthService INSTANCE = new AuthService();


    /* =================== Регистрация и верификация =================== */

    /**
     * Отправляет запрос на регистрацию пользователя.
     * @param req DTO с регистрационными данными
     * @return ApiResponse с результатом регистрации
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     * @throws IllegalArgumentException если сервер вернул ошибку регистрации
     */
    public ApiResponse register(RegistrationRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "register", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    /**
     * Подтверждает email пользователя с помощью кода из письма.
     * @param req DTO с email и кодом
     * @return ApiResponse с результатом подтверждения
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     * @throws IllegalArgumentException если сервер вернул ошибку
     */
    public ApiResponse verifyEmail(VerificationRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "verify-email", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    /* =================== Авторизация =================== */

    /**
     * Выполняет вход пользователя (логин/email и пароль) и сохраняет токены.
     * @param req DTO с логином/email и паролем
     * @return LoginResponse с accessToken, refreshToken, tokenType
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     * @throws IllegalArgumentException если сервер вернул ошибку авторизации
     */
    public LoginResponse login(LoginRequest req) throws IOException, InterruptedException {
        try {
            var resp = post(baseUri, "login", req, LoginResponse.class, false, null);
            // Сохраняем оба токена
            SessionManager.getInstance().saveTokens(resp.accessToken(), resp.refreshToken());
            return resp;
        } catch (Exception ex) {
            throw new IllegalArgumentException(extractErrorMessage(ex));
        }
    }


    /* =================== Сброс пароля =================== */

    /**
     * Инициирует сброс пароля (отправляет email с кодом).
     * @param req DTO с email
     * @return ApiResponse с результатом
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     * @throws IllegalArgumentException если сервер вернул ошибку
     */
    public ApiResponse resetPasswordRequest(PasswordResetRequest req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "password-reset", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }

    /**
     * Подтверждает сброс пароля (email, код и новый пароль).
     * @param req DTO с email, кодом и новым паролем
     * @return ApiResponse с результатом
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     * @throws IllegalArgumentException если сервер вернул ошибку
     */
    public ApiResponse resetPasswordConfirm(PasswordResetConfirm req) throws IOException, InterruptedException {
        try {
            return post(baseUri, "password-reset/confirm", req, ApiResponse.class, false, null);
        } catch (Exception e) {
            throw new IllegalArgumentException(extractErrorMessage(e));
        }
    }


    /**
     * Отменяет незавершённую регистрацию пользователя по email (асинхронно, без ожидания ответа).
     * @param email email пользователя
     */
    public void cancelRegistration(String email) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_REGISTRATION))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Отменяет процесс сброса пароля по email (асинхронно, без ожидания ответа).
     * @param email email пользователя
     */
    public void cancelPasswordReset(String email) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_PASSWORD_RESET))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Извлекает человеко-читаемое сообщение об ошибке из исключения или JSON-ответа.
     * @param ex исключение, выброшенное при запросе
     * @return текст ошибки для пользователя
     */
    String extractErrorMessage(Exception ex) {
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
        return msg;
    }

    /**
     * Выполняет выход пользователя: отправляет refreshToken на сервер и очищает локальное хранилище токенов.
     * @throws IOException ошибка ввода-вывода при запросе
     * @throws InterruptedException если поток прерван
     */
    public void logout() throws IOException, InterruptedException {
        String refreshToken = SessionManager.getInstance().getRefreshToken();
        if (refreshToken != null) {
            try {
                var req = new RefreshTokenRequest(refreshToken);
                post(baseUri, "logout", req, ApiResponse.class, false, null);
            } catch (Exception ex) {
                // Игнорируем ошибку выхода (например, если refreshToken уже невалиден)
            }
        }
        SessionManager.getInstance().logout();
    }

    /**
     * Обновляет access и refresh токены по refreshToken.
     * @return true, если токены успешно обновлены, false если refreshToken невалиден
     * @throws IOException ошибка запроса
     * @throws InterruptedException если поток прерван
     */
    public boolean refreshTokens() throws IOException, InterruptedException {
        String refreshToken = SessionManager.getInstance().getRefreshToken();
        if (refreshToken == null) return false;

        try {
            var req = new RefreshTokenRequest(refreshToken);
            var resp = post(baseUri, "refresh", req, RefreshTokenResponse.class, false, null);
            if (resp != null && resp.accessToken() != null && resp.refreshToken() != null) {
                SessionManager.getInstance().saveTokens(resp.accessToken(), resp.refreshToken());
                return true;
            }
        } catch (Exception ex) {
            // Если сервер сообщил об ошибке refreshToken — считаем, что refreshToken невалиден, удаляем сессию
            SessionManager.getInstance().logout();

        }
        return false;
    }

    /**
     * Получить singleton‑инстанс AuthService.
     * @return экземпляр AuthService
     */
    public static AuthService getInstance() {
        return INSTANCE;
    }


}
