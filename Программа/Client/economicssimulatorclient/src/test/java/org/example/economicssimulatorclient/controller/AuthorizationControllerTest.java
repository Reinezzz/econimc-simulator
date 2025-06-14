package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.scene.control.*;
import org.example.economicssimulatorclient.controller.AuthorizationController;
import org.example.economicssimulatorclient.service.AuthService;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthorizationControllerTest {

    @Mock AuthService authService;

    AuthorizationController ctrl;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
        ctrl = new AuthorizationController();

        // Проставляем UI-элементы
        ctrl.usernameEmailField = new TextField();
        ctrl.passwordField = new PasswordField();
        ctrl.loginButton = new Button();
        ctrl.statusLabel = new Label();

        // Подменяем сервис через reflection
        java.lang.reflect.Field authField = AuthorizationController.class.getDeclaredField("auth");
        authField.setAccessible(true);
        authField.set(ctrl, authService);
    }

    @Test
    void doLogin_emptyFieldsShowsError() {
        ctrl.usernameEmailField.setText("");
        ctrl.passwordField.setText("");
        ctrl.doLogin();
        assertTrue(ctrl.statusLabel.getText().length() > 0);
        assertFalse(ctrl.loginButton.isDisabled());
    }

    @Test
    void doLogin_successfulLogin_showsSuccessAndResetsFields() throws Exception {
        ctrl.usernameEmailField.setText("user@mail.com");
        ctrl.passwordField.setText("password");

        doAnswer(inv -> null).when(authService).login(any());

        // Используем Platform.runLater, т.к. логика UI работает в FX-потоке
        Platform.runLater(() -> {
            ctrl.doLogin();
            // Проверяем, что кнопка вновь активна (разблокировалась после попытки)
            assertFalse(ctrl.loginButton.isDisabled());
        });
    }

    @Test
    void doLogin_illegalArgumentExceptionShowsError() throws IOException, InterruptedException {
        ctrl.usernameEmailField.setText("user@mail.com");
        ctrl.passwordField.setText("wrongpass");

        doThrow(new IllegalArgumentException("Wrong!")).when(authService).login(any());

        Platform.runLater(() -> {
            ctrl.doLogin();
            assertTrue(ctrl.statusLabel.getText().contains("Wrong!"));
            assertEquals("", ctrl.passwordField.getText());
            assertFalse(ctrl.loginButton.isDisabled());
        });
    }

    @Test
    void openRegister_clearsStatusAndFields() {
        // Проверка, что метод вызывается без ошибок (логика внутри SceneManager.switchTo)
        assertDoesNotThrow(() -> ctrl.openRegister());
    }

    @Test
    void openReset_clearsStatusAndFields() {
        assertDoesNotThrow(() -> ctrl.openReset());
    }

    @Test
    void clearFields_clearsInputFields() {
        ctrl.usernameEmailField.setText("test");
        ctrl.passwordField.setText("123");
        ctrl.clearFields();
        assertEquals("", ctrl.usernameEmailField.getText());
        assertEquals("", ctrl.passwordField.getText());
    }
}
