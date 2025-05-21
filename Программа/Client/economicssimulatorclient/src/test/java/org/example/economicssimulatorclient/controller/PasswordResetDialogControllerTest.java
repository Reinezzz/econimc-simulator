package org.example.economicssimulatorclient.controller;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetDialogControllerTest {

    private PasswordResetDialogController controller;

    @BeforeEach
    void setUp() {
        controller = new PasswordResetDialogController();
        controller.dialogPane = new DialogPane();
        controller.codeField = new TextField();
        controller.passField = new PasswordField();
        controller.repeatField = new PasswordField();
        controller.errorLabel = new Label();
        controller.okBtn = ButtonType.OK;
    }

    @Test
    void testGetCode_and_GetPassword() {
        controller.codeField.setText("123456");
        controller.passField.setText("Password1!");
        assertEquals("123456", controller.getCode());
        assertEquals("Password1!", controller.getPassword());
    }

    // setupValidation и взаимодействие с UI - тестировать интеграционно или с TestFX.
}
