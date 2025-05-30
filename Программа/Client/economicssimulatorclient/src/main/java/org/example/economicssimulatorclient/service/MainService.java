package org.example.economicssimulatorclient.service;

import javafx.application.Platform;
import org.example.economicssimulatorclient.controller.AuthorizationController;
import org.example.economicssimulatorclient.controller.BaseController;
import org.example.economicssimulatorclient.util.SceneManager;
import org.example.economicssimulatorclient.util.SessionManager;

import java.io.IOException;
import java.net.URI;

/**
 * Базовый сервис для основного экрана и всех его потомков.
 * Все методы post/get всегда авторизованы, при 401 — попытка refresh токена.
 */
public class MainService extends BaseService {

    /**
     * Авторизованный POST-запрос с авто-refresh токена при 401.
     */
    @Override
    protected <T> T post(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {
        // Игнорируем параметры auth и accessToken, всегда используем авторизацию через текущий accessToken
        String currentToken = SessionManager.getInstance().getAccessToken();
        try {
            return super.post(baseUri, endpoint, body, respType, true, currentToken);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("HTTP 401")) {
                if (AuthService.getInstance().refreshTokens()) {
                    currentToken = SessionManager.getInstance().getAccessToken();
                    return super.post(baseUri, endpoint, body, respType, true, currentToken);
                } else {
                    AuthService.getInstance().logout();
                    Platform.runLater(() -> SceneManager.switchTo("authorization.fxml", c -> ((BaseController) c).clearFields()));
                    throw new IllegalArgumentException("Сессия истекла, войдите заново");
                }
            }
            throw ex;
        }
    }

    /**
     * Авторизованный GET-запрос с авто-refresh токена при 401.
     */
    @Override
    protected <T> T get(
            URI baseUri,
            String endpoint,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {
        // Игнорируем параметры auth и accessToken, всегда используем авторизацию через текущий accessToken
        String currentToken = SessionManager.getInstance().getAccessToken();
        try {
            return super.get(baseUri, endpoint, respType, true, currentToken);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("HTTP 401")) {
                if (AuthService.getInstance().refreshTokens()) {
                    currentToken = SessionManager.getInstance().getAccessToken();
                    return super.get(baseUri, endpoint, respType, true, currentToken);
                } else {
                    AuthService.getInstance().logout();
                    Platform.runLater(() -> SceneManager.switchTo("authorization.fxml", c -> ((BaseController) c).clearFields()));
                    throw new IllegalArgumentException("Сессия истекла, войдите заново");
                }
            }
            throw ex;
        }
    }

    @Override
    protected <T> T put(
            URI baseUri,
            String endpoint,
            Object body,
            Class<T> respType,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {
        String currentToken = SessionManager.getInstance().getAccessToken();
        try {
            return super.put(baseUri, endpoint, body, respType, true, currentToken);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("HTTP 401")) {
                if (AuthService.getInstance().refreshTokens()) {
                    currentToken = SessionManager.getInstance().getAccessToken();
                    return super.put(baseUri, endpoint, body, respType, true, currentToken);
                } else {
                    AuthService.getInstance().logout();
                    Platform.runLater(() -> SceneManager.switchTo("authorization.fxml", c -> ((BaseController) c).clearFields()));
                    throw new IllegalArgumentException("Сессия истекла, войдите заново");
                }
            }
            throw ex;
        }
    }

    @Override
    protected void delete(
            URI baseUri,
            String endpoint,
            boolean auth,
            String accessToken
    ) throws IOException, InterruptedException {
        String currentToken = SessionManager.getInstance().getAccessToken();
        try {
            super.delete(baseUri, endpoint, true, currentToken);
        } catch (RuntimeException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("HTTP 401")) {
                if (AuthService.getInstance().refreshTokens()) {
                    currentToken = SessionManager.getInstance().getAccessToken();
                    super.delete(baseUri, endpoint, true, currentToken);
                } else {
                    AuthService.getInstance().logout();
                    Platform.runLater(() -> SceneManager.switchTo("authorization.fxml", c -> ((BaseController) c).clearFields()));
                    throw new IllegalArgumentException("Сессия истекла, войдите заново");
                }
            } else {
                throw ex;
            }
        }
    }

}
