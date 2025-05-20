package org.example.economicssimulatorclient.controller;

import org.example.economicssimulatorclient.dto.LoginRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AuthorizationController extends BaseController {

    @FXML
    private Hyperlink forgotLink;
    @FXML
    private TextField usernameEmailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Label statusLabel;

    private final AuthService auth = BaseController.get(AuthService.class);


    @FXML
    private void doLogin() {
        showError(statusLabel, ""); // очистка статуса
        String login = usernameEmailField.getText().trim();
        String pass = passwordField.getText();

        if (login.isEmpty() || pass.isEmpty()) {
            showError(statusLabel, "common.status_field");
            return;
        }

        loginButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.login(new LoginRequest(login, pass));
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "auth.status_label.successful");
//                    SceneManager.switchTo("main.fxml");
                });
            } catch (IllegalArgumentException ex) {
                Platform.runLater(() -> {
                    showError(statusLabel, tr("error.base") + ex.getMessage()); // общий текст ошибки
                    passwordField.setText("");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, tr("error.base") + ex.getMessage()));
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
