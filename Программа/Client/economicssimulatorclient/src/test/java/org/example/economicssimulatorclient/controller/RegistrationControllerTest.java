package org.example.economicssimulatorclient.controller;

import javafx.scene.control.*;
import org.example.economicssimulatorclient.controller.RegistrationController;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class RegistrationControllerTest {
    RegistrationController ctrl;

    @BeforeEach
    void setup() {
        ctrl = new RegistrationController();
        ctrl.usernameField = new TextField();
        ctrl.emailField = new TextField();
        ctrl.passwordField = new PasswordField();
        ctrl.repeatPasswordField = new PasswordField();
        ctrl.registerButton = new Button();
        ctrl.statusLabel = new Label();
    }

    @Test
    void clearFields_emptiesFields() {
        ctrl.usernameField.setText("x");
        ctrl.emailField.setText("y");
        ctrl.passwordField.setText("z");
        ctrl.repeatPasswordField.setText("w");
        ctrl.clearFields();
        assertEquals("", ctrl.usernameField.getText());
        assertEquals("", ctrl.emailField.getText());
        assertEquals("", ctrl.passwordField.getText());
        assertEquals("", ctrl.repeatPasswordField.getText());
    }

    @Test
    void doRegister_passwordsMismatchShowsError() {
        ctrl.usernameField.setText("user");
        ctrl.emailField.setText("mail@mail.com");
        ctrl.passwordField.setText("12345678Aa!");
        ctrl.repeatPasswordField.setText("differentPass");
        ctrl.doRegister();
        assertFalse(ctrl.statusLabel.getText().isEmpty());
    }

    @Test
    void doRegister_weakPasswordShowsError() {
        ctrl.usernameField.setText("user");
        ctrl.emailField.setText("mail@mail.com");
        ctrl.passwordField.setText("123");
        ctrl.repeatPasswordField.setText("123");
        ctrl.doRegister();
        assertFalse(ctrl.statusLabel.getText().isEmpty());
    }
}
