package org.example.economicssimulatorclient.util;

import lombok.Getter;
import org.example.economicssimulatorclient.config.AppConfig;

import java.net.URI;
import java.util.prefs.Preferences;

/**
 * Управление сессией пользователя.
 * Хранит токены в {@link java.util.prefs.Preferences} и предоставляет простые методы для входа/выхода.
 * Адрес сервера берётся из {@link org.example.economicssimulatorclient.config.AppConfig}.
 */
public class SessionManager {

    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static SessionManager INSTANCE = new SessionManager();
    @Getter
    private boolean justLoggedOut = false;

    private final Preferences prefs;

    /**
     * Создаёт менеджер сессии и инициализирует хранилище предпочтений.
     */
    public SessionManager() {
        prefs = Preferences.userNodeForPackage(SessionManager.class);
    }

    /**
     * Возвращает единственный экземпляр менеджера.
     */
    public static SessionManager getInstance() {
        return INSTANCE;
    }

    /**
     * Позволяет подменить экземпляр менеджера, например, в тестах.
     */
    public static void setInstance(SessionManager mockSessionManager) {
        INSTANCE = mockSessionManager;
    }

    /**
     * Сохраняет токены в хранилище настроек.
     */
    public void saveTokens(String accessToken, String refreshToken) {
        prefs.put(ACCESS_TOKEN_KEY, accessToken);
        prefs.put(REFRESH_TOKEN_KEY, refreshToken);
    }

    /**
     * Возвращает сохранённый access token.
     */
    public String getAccessToken() {
        return prefs.get(ACCESS_TOKEN_KEY, null);
    }

    /**
     * Возвращает сохранённый refresh token.
     */
    public String getRefreshToken() {
        return prefs.get(REFRESH_TOKEN_KEY, null);
    }

    /**
     * Удаляет токены и помечает состояние как разлогинившееся.
     */
    public void logout() {
        prefs.remove(ACCESS_TOKEN_KEY);
        prefs.remove(REFRESH_TOKEN_KEY);
        justLoggedOut = true;
    }

    /**
     * Сбрасывает флаг {@link #justLoggedOut} после обработки выхода.
     */
    public void resetJustLoggedOut() {
        justLoggedOut = false;
    }

    /**
     * Возвращает базовый адрес сервера из конфигурации приложения.
     */
    public URI getBaseUri() {
        return URI.create(AppConfig.getBaseUrl());
    }
}
