package org.example.economicssimulatorclient.controller;

import org.example.economicssimulatorclient.dto.LoginRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.service.EconomicModelService;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Контроллер окна авторизации. Выполняет проверку учётных данных и
 * предоставляет переход к экранам регистрации и восстановления пароля.
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
     * Инициализирует контроллер и настраивает кнопку смены языка.
     */
    @FXML
    private void initialize() {
        initLangButton();
    }

    /**
     * Пытается выполнить вход. При успехе открывает главный экран и
     * создаёт экземпляр {@link EconomicModelService}. Ошибки валидации и
     * исключения сервиса выводятся в поле состояния.
     */
    @FXML
    public void doLogin() {
        showError(statusLabel, "");
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
                    BaseController.provide(EconomicModelService.class, new EconomicModelService());
                    SceneManager.switchToWithoutContorller("main.fxml");
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
     * Открывает сцену регистрации и очищает сообщения статуса в новом
     * контроллере.
     */
    @FXML
    public void openRegister() {
        SceneManager.switchTo("registration.fxml", c -> {
            ((BaseController) c).clearStatusLabel();
            ((BaseController) c).clearFields();
        });
    }

    /**
     * Открывает форму смены пароля, предварительно очищая её поля и
     * сообщения статуса.
     */
    @FXML
    public void openReset() {
        SceneManager.switchTo("password_change.fxml", c -> {
            ((BaseController) c).clearStatusLabel();
            ((BaseController) c).clearFields();
        });
    }

    /**
     * Очищает поля логина и пароля.
     */
    @Override
    public void clearFields() {
        if (usernameEmailField != null) usernameEmailField.clear();
        if (passwordField != null) passwordField.clear();
    }
}
