package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.config.AppConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.prefs.Preferences;

import static org.assertj.core.api.Assertions.assertThat;

class SessionManagerTest {

    @BeforeEach
    void cleanPrefs() {
        // Подмени инстанс на свой, если используешь mock
        SessionManager.getInstance().logout();
        SessionManager.getInstance().resetJustLoggedOut();
    }

    @AfterEach
    void cleanup() {
        SessionManager.getInstance().logout();
    }

    @Test
    void saveAndGetTokens() {
        var session = SessionManager.getInstance();
        session.saveTokens("access", "refresh");

        assertThat(session.getAccessToken()).isEqualTo("access");
        assertThat(session.getRefreshToken()).isEqualTo("refresh");
    }

    @Test
    void logoutRemovesTokensAndSetsFlag() {
        var session = SessionManager.getInstance();
        session.saveTokens("a", "b");
        session.logout();

        assertThat(session.getAccessToken()).isNull();
        assertThat(session.getRefreshToken()).isNull();
        assertThat(session.isJustLoggedOut()).isTrue();

        session.resetJustLoggedOut();
        assertThat(session.isJustLoggedOut()).isFalse();
    }

    @Test
    void getBaseUriReturnsConfiguredBaseUrl() {
        // Мокаем baseUrl через AppConfig если потребуется (например, через setter или static поле)
        URI uri = SessionManager.getInstance().getBaseUri();
        assertThat(uri).isNotNull();
        // Можно проверить ожидаемое значение если известна дефолтная база
    }

    @Test
    void canSetInstanceForTesting() {
        SessionManager custom = new SessionManager() {
            @Override public String getAccessToken() { return "mocked"; }
        };
        SessionManager.setInstance(custom);
        assertThat(SessionManager.getInstance().getAccessToken()).isEqualTo("mocked");

        // Восстановить default singleton (важно для изоляции других тестов)
        SessionManager.setInstance(new SessionManager());
    }
}
