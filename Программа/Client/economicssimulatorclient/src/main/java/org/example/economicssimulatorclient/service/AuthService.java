package org.example.economicssimulatorclient.service;

import org.example.economicssimulatorclient.config.AppConfig;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.JsonUtil;
import org.example.economicssimulatorclient.util.SessionManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService extends BaseService {

    private static final String AUTH_PATH = "/auth";
    private static final String AUTH_CANCEL_REGISTRATION = "cancel-registration";
    private static final String AUTH_CANCEL_PASSWORD_RESET = "cancel-password-reset";
    private final URI baseUri = URI.create(AppConfig.getBaseUrl() + AUTH_PATH + "/");

    private static final AuthService INSTANCE = new AuthService();


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

    public LoginResponse login(LoginRequest req) throws IOException, InterruptedException {
        try {
            var resp = post(baseUri, "login", req, LoginResponse.class, false, null);
            SessionManager.getInstance().saveTokens(resp.accessToken(), resp.refreshToken());
            SessionManager.getInstance().resetJustLoggedOut();
            return resp;
        } catch (Exception ex) {
            throw new IllegalArgumentException(extractErrorMessage(ex));
        }
    }

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

    public void cancelRegistration(String email) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_REGISTRATION))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

    public void cancelPasswordReset(String email) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(baseUri.resolve(AUTH_CANCEL_PASSWORD_RESET))
                .POST(HttpRequest.BodyPublishers.ofString("{\"email\":\"" + email + "\"}"))
                .header("Content-Type", "application/json")
                .header("Accept-Language", I18n.getLocale().getLanguage())
                .build();
        httpClient.sendAsync(req, HttpResponse.BodyHandlers.discarding());
    }

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

    public void logout() throws IOException, InterruptedException {
        String refreshToken = SessionManager.getInstance().getRefreshToken();

        SessionManager.getInstance().logout();
        if (refreshToken != null) {
            try {
                var req = new RefreshTokenRequest(refreshToken);
                post(baseUri, "logout", req, ApiResponse.class, false, null);
            } catch (Exception ex) {
            }
        }
    }

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
            SessionManager.getInstance().logout();

        }
        return false;
    }

    public static AuthService getInstance() {
        return INSTANCE;
    }


}
