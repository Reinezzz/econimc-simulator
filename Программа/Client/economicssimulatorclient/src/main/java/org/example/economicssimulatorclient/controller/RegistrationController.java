package org.example.economicssimulatorclient.controller;

import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrationController {

    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;
    @FXML private Hyperlink loginLink;
    @FXML private Label statusLabel;

    private final AuthService auth = new AuthService();

    @FXML
    private void initialize() {
        loginLink.setOnAction(e -> SceneManager.switchTo("authorization.fxml"));
        registerButton.setOnAction(e -> doRegister());
    }

    private void doRegister() {
        statusLabel.setText("");
        String u = usernameField.getText().trim();
        String e = emailField.getText().trim();
        String p = passwordField.getText();
        if (u.isEmpty() || e.isEmpty() || p.isEmpty()) {
            statusLabel.setText("Fill all fields");
            return;
        }
        registerButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.register(new RegistrationRequest(u, e, p));
                Platform.runLater(() -> askCode(e));
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Error: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> registerButton.setDisable(false));
            }
        }).start();
    }

    private void askCode(String email) {
        String code = SceneManager.showInputDialog(
                "Verification", "Enter code sent to " + email, "Code:");
        if (code == null) return;  // cancel
        new Thread(() -> {
            try {
                auth.verifyEmail(new VerificationRequest(email, code.trim()));
                Platform.runLater(() -> {
                    statusLabel.setText("Verified! You can login.");
                    SceneManager.switchTo("authorization.fxml");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Wrong code"));
            }
        }).start();
    }
}
