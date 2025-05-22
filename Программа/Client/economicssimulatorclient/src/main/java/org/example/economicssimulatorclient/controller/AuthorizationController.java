package org.example.economicssimulatorclient.controller;

import javafx.scene.layout.StackPane;
import org.example.economicssimulatorclient.dto.LoginRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Контроллер экрана авторизации пользователя.
 * Отвечает за обработку логина, переход к регистрации и сбросу пароля.
 */
public class AuthorizationController extends BaseController {

    @FXML
    Hyperlink forgotLink;
    @FXML
    TextField usernameEmailField;
    @FXML
    PasswordField passwordField;
    @FXML
    Button loginButton;
    @FXML
    Label statusLabel;

    private final AuthService auth = BaseController.get(AuthService.class);

    /**
     * Обрабатывает попытку входа пользователя по нажатию кнопки.
     * Выполняет валидацию, асинхронный вызов сервиса и отображение результата.
     */
    @FXML
    void doLogin() {
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
                    // SceneManager.switchTo("main.fxml");
                });
            } catch (IllegalArgumentException ex) {
                Platform.runLater(() -> {
                    showError(statusLabel, tr("error.base") + ex.getMessage());
                    passwordField.setText("");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, tr("error.base") + ex.getMessage()));
            } finally {
                Platform.runLater(() -> loginButton.setDisable(false));
            }
        }).start();
    }

    /**
     * Переход на экран регистрации.
     */
    @FXML
    private void openRegister() {
        SceneManager.switchTo("registration.fxml");
    }

    /**
     * Переход на экран сброса пароля.
     */
    @FXML
    private void openReset() {
        SceneManager.switchTo("password_change.fxml");
    }
}
