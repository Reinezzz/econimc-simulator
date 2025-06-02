package org.example.economicssimulatorclient.util;

import lombok.Getter;

import java.util.prefs.Preferences;

/**
 * Класс для управления сессией пользователя (accessToken, refreshToken).
 * Использует Java Preferences API для хранения токенов.
 */
public class SessionManager {

    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static SessionManager INSTANCE = new SessionManager();
    @Getter
    private boolean justLoggedOut = false;

    private final Preferences prefs;

    private SessionManager() {
        prefs = Preferences.userNodeForPackage(SessionManager.class);
    }

    /**
     * Получить singleton-инстанс SessionManager.
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static Long getCurrentUserId() {
        return null;
    }

    public static void setInstance(SessionManager mockSessionManager) {
        INSTANCE = mockSessionManager;
    }

    /**
     * Сохраняет access и refresh токены.
     * @param accessToken access токен (JWT)
     * @param refreshToken refresh токен
     */
    public void saveTokens(String accessToken, String refreshToken) {
        prefs.put(ACCESS_TOKEN_KEY, accessToken);
        prefs.put(REFRESH_TOKEN_KEY, refreshToken);
    }

    /**
     * Получить access токен.
     */
    public String getAccessToken() {
        return prefs.get(ACCESS_TOKEN_KEY, null);
    }

    /**
     * Получить refresh токен.
     */
    public String getRefreshToken() {
        return prefs.get(REFRESH_TOKEN_KEY, null);
    }

    /**
     * Очищает сохранённые токены (logout).
     */
    public void logout() {
        prefs.remove(ACCESS_TOKEN_KEY);
        prefs.remove(REFRESH_TOKEN_KEY);
        justLoggedOut = true;
    }

    public void resetJustLoggedOut() {
        justLoggedOut = false;
    }

    /**
     * Проверяет, авторизован ли пользователь.
     * @return true, если есть access и refresh токены
     */
    public boolean isLoggedIn() {
        return getAccessToken() != null && getRefreshToken() != null;
    }

}
