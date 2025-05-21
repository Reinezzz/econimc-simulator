package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.economicssimulatorclient.dto.RegistrationRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

class RegistrationControllerTest {

    private RegistrationController controller;
    private AuthService authServiceMock;

    @BeforeEach
    void setUp() {
        controller = new RegistrationController();
        controller.statusLabel = new Label();
        controller.usernameField = new TextField();
        controller.emailField = new TextField();
        controller.passwordField = new PasswordField();
        controller.repeatPasswordField = new PasswordField();
        controller.registerButton = new Button();

        authServiceMock = mock(AuthService.class);
        BaseController.provide(AuthService.class, authServiceMock);
    }

    @Test
    void testDoRegister_emptyFields_showsError() {
        controller.usernameField.setText("");
        controller.emailField.setText("");
        controller.passwordField.setText("");
        controller.repeatPasswordField.setText("");
        controller.doRegister();
        assertTrue(controller.statusLabel.getText().length() > 0);
    }

    @Test
    void testDoRegister_invalidEmail_showsError() {
        controller.usernameField.setText("user");
        controller.emailField.setText("bademail");
        controller.passwordField.setText("Password1!");
        controller.repeatPasswordField.setText("Password1!");
        controller.doRegister();
        assertTrue(controller.statusLabel.getText().length() > 0);
    }

    @Test
    void testDoRegister_weakPassword_showsError() {
        controller.usernameField.setText("user");
        controller.emailField.setText("user@mail.com");
        controller.passwordField.setText("123");
        controller.repeatPasswordField.setText("123");
        controller.doRegister();
        assertTrue(controller.statusLabel.getText().length() > 0);
    }

    @Test
    void testDoRegister_passwordMismatch_showsError() {
        controller.usernameField.setText("user");
        controller.emailField.setText("user@mail.com");
        controller.passwordField.setText("Password1!");
        controller.repeatPasswordField.setText("Password2!");
        controller.doRegister();
        assertTrue(controller.statusLabel.getText().length() > 0);
    }

    // Для валидного запроса — интеграционный тест
}
