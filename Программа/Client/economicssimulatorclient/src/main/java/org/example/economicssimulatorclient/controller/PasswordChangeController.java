package org.example.economicssimulatorclient.controller;

import org.example.economicssimulatorclient.dto.PasswordResetConfirm;
import org.example.economicssimulatorclient.dto.PasswordResetRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordChangeController {

    @FXML private TextField emailField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button sendCodeButton;
    @FXML private Button resetButton;
    @FXML private Hyperlink backLink;
    @FXML private Label statusLabel;

    private final AuthService auth = new AuthService();
    private String lastEmail;          // для подтверждения

    @FXML
    private void initialize() {
        sendCodeButton.setOnAction(e -> sendCode());
        resetButton.setOnAction(e -> reset());
        backLink.setOnAction(e -> SceneManager.switchTo("authorization.fxml"));
    }

    private void sendCode() {
        statusLabel.setText("");
        String email = emailField.getText().trim();
        if (email.isEmpty()) { statusLabel.setText("Enter email"); return; }
        sendCodeButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.resetPasswordRequest(new PasswordResetRequest(email));
                lastEmail = email;
                Platform.runLater(() -> statusLabel.setText("Code sent!"));
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> sendCodeButton.setDisable(false));
            }
        }).start();
    }

    private void reset() {
        if (lastEmail == null) { statusLabel.setText("First send code"); return; }
        String code = SceneManager.showInputDialog(
                "Reset password", "Code sent to " + lastEmail, "Code:");
        if (code == null) return;
        String newPass = newPasswordField.getText();
        if (newPass.isEmpty()) { statusLabel.setText("Enter new password"); return; }

        resetButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.resetPasswordConfirm(
                        new PasswordResetConfirm(lastEmail, code.trim(), newPass));
                Platform.runLater(() -> {
                    statusLabel.setText("Password updated!");
                    SceneManager.switchTo("authorization.fxml");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> resetButton.setDisable(false));
            }
        }).start();
    }
}
