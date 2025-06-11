package org.example.economicssimulatorclient.util;

import lombok.Getter;
import org.example.economicssimulatorclient.config.AppConfig;

import java.net.URI;
import java.util.prefs.Preferences;

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

    public static SessionManager getInstance() {
        return INSTANCE;
    }

    public static Long getCurrentUserId() {
        return null;
    }

    public static void setInstance(SessionManager mockSessionManager) {
        INSTANCE = mockSessionManager;
    }

    public void saveTokens(String accessToken, String refreshToken) {
        prefs.put(ACCESS_TOKEN_KEY, accessToken);
        prefs.put(REFRESH_TOKEN_KEY, refreshToken);
    }

    public String getAccessToken() {
        return prefs.get(ACCESS_TOKEN_KEY, null);
    }

    public String getRefreshToken() {
        return prefs.get(REFRESH_TOKEN_KEY, null);
    }

    public void logout() {
        prefs.remove(ACCESS_TOKEN_KEY);
        prefs.remove(REFRESH_TOKEN_KEY);
        justLoggedOut = true;
    }

    public void resetJustLoggedOut() {
        justLoggedOut = false;
    }

    public boolean isLoggedIn() {
        return getAccessToken() != null && getRefreshToken() != null;
    }

    public URI getBaseUri() {
        return URI.create(AppConfig.getBaseUrl());
    }
}
