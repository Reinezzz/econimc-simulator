package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import org.example.economicssimulatorclient.dto.LoginRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthorizationControllerTest {

    private AuthorizationController controller;
    private AuthService authServiceMock;

    @BeforeEach
    void setUp() {
        controller = new AuthorizationController();

        // Mock UI elements
        controller.statusLabel = new Label();
        controller.usernameEmailField = new TextField();
        controller.passwordField = new PasswordField();
        controller.loginButton = new Button();

        // Mock AuthService
        authServiceMock = mock(AuthService.class);
        // Подменяем DI, если есть метод для внедрения - лучше использовать его.
        BaseController.provide(AuthService.class, authServiceMock);
    }

    @Test
    void testDoLogin_emptyFields_showsError() {
        controller.usernameEmailField.setText("");
        controller.passwordField.setText("");
        controller.doLogin();
        assertTrue(controller.statusLabel.getText().length() > 0);
    }

    @Test
    void testDoLogin_validCredentials_success() throws Exception {
        controller.usernameEmailField.setText("test@test.com");
        controller.passwordField.setText("password");

        when(authServiceMock.login(any(LoginRequest.class)))
                .thenReturn(null);

        controller.doLogin();
        // Статус обновится асинхронно, смотри TestFX для JavaFX UI thread
    }

    @Test
    void testDoLogin_invalidCredentials_showsError() throws Exception {
        controller.usernameEmailField.setText("test@test.com");
        controller.passwordField.setText("badpass");
        when(authServiceMock.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("Wrong credentials"));

        controller.doLogin();
        // Аналогично, нужен TestFX для UI thread проверки
    }

    // Аналогично добавляются тесты для openRegister и openReset, если нужно проверить вызовы SceneManager
}
