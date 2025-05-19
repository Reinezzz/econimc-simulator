package org.example.economicssimulatorclient.controller;

import org.example.economicssimulatorclient.dto.LoginRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthorizationController {

    @FXML private Hyperlink forgotLink;
    @FXML private TextField   usernameEmailField;
    @FXML private PasswordField passwordField;
    @FXML private Button      loginButton;
    @FXML private Label       statusLabel;

    private final AuthService auth = new AuthService();

    /* ====== обработчики, указанные напрямую в FXML ====== */

    @FXML
    private void doLogin() {
        statusLabel.setText("");
        String login = usernameEmailField.getText().trim();
        String pass  = passwordField.getText();

        if (login.isEmpty() || pass.isEmpty()) {
            statusLabel.setText(I18n.t("common.status_field"));
            return;
        }

        loginButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.login(new LoginRequest(login, pass));
                Platform.runLater(() -> {
                    statusLabel.setText(I18n.t("auth.status_label.successful"));
                    SceneManager.switchTo("main.fxml");   // переход дальше
                });
            } catch (IllegalArgumentException ex) {
                Platform.runLater(() -> statusLabel.setText(I18n.t("error.base") + ex.getMessage()));
                Platform.runLater(() -> {passwordField.setText("");});
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText(I18n.t("error.base") + ex.getMessage()));
            } finally {
                Platform.runLater(() -> loginButton.setDisable(false));
            }
        }).start();
    }

    @FXML
    private void openRegister() {
        SceneManager.switchTo("registration.fxml");
    }

    @FXML
    private void openReset() {
        SceneManager.switchTo("password_change.fxml");
    }
}
