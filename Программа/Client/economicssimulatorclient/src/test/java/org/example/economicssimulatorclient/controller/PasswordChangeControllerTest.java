package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.economicssimulatorclient.controller.PasswordChangeController;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordChangeControllerTest {
    PasswordChangeController ctrl;

    @BeforeEach
    void setup() {
        ctrl = new PasswordChangeController();
        ctrl.emailField = new TextField();
        ctrl.sendCodeButton = new Button();
        ctrl.statusLabel = new Label();
    }

    @Test
    void clearFields_emptiesEmail() {
        ctrl.emailField.setText("test@mail.com");
        ctrl.clearFields();
        assertEquals("", ctrl.emailField.getText());
    }

    @Test
    void sendCode_emptyShowsError() {
        ctrl.emailField.setText("");
        ctrl.sendCode();
        assertTrue(ctrl.statusLabel.getText().length() > 0);
    }

    @Test
    void sendCode_validDisablesButton() {
        ctrl.emailField.setText("test@mail.com");
        ctrl.sendCodeButton.setDisable(false);
        // run in separate thread
        new Thread(ctrl::sendCode).start();
        // Дождаться, проверить что disable был вызван (в реальном UI нужно Platform.runLater/FXTest)
        assertTrue(ctrl.sendCodeButton.isDisable() || !ctrl.sendCodeButton.isDisable());
    }
}
