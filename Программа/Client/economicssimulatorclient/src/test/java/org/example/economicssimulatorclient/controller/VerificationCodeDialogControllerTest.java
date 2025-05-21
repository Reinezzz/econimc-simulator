package org.example.economicssimulatorclient.controller;

import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationCodeDialogControllerTest {

    private VerificationCodeDialogController controller;

    @BeforeEach
    void setUp() {
        controller = new VerificationCodeDialogController();
        controller.dialogPane = new DialogPane();
        controller.codeField = new TextField();
        controller.errorLabel = new Label();
        controller.okBtn = ButtonType.OK;
        controller.cancelBtn = ButtonType.CANCEL;
    }

    @Test
    void testGetCode_returnsTrimmedCode() {
        controller.codeField.setText(" 123456 ");
        assertEquals("123456", controller.getCode());
    }

    // UI-тесты для enable/disable кнопок и валидации — смотри TestFX.
}
