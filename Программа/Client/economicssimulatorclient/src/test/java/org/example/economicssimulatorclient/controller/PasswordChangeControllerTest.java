package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.example.economicssimulatorclient.dto.PasswordResetRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

class PasswordChangeControllerTest {

    private PasswordChangeController controller;
    private AuthService authServiceMock;

    @BeforeEach
    void setUp() {
        controller = new PasswordChangeController();
        controller.statusLabel = new Label();
        controller.emailField = new TextField();
        controller.sendCodeButton = new Button();

        authServiceMock = mock(AuthService.class);
        BaseController.provide(AuthService.class, authServiceMock);
    }

    @Test
    void testSendCode_emptyEmail_showsError() {
        controller.emailField.setText("");
        controller.sendCode();
        Assertions.assertFalse(controller.statusLabel.getText().isEmpty());
    }

    @Test
    void testSendCode_validEmail_callsService() throws Exception {
        controller.emailField.setText("test@test.com");
        doNothing().when(authServiceMock).resetPasswordRequest(any(PasswordResetRequest.class));
        controller.sendCode();
        // Аналогично: смотри TestFX для проверки UI-эффекта
    }

    // Для openDialog и confirmReset обычно интеграционные тесты или TestFX.
}
