package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.dto.PasswordResetConfirm;
import org.example.economicssimulatorclient.dto.PasswordResetRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.util.Pair;

import java.util.ResourceBundle;

/**
 * Контроллер страницы восстановления пароля. Отправляет код на указанный
 * email и открывает диалог подтверждения.
 */
public class PasswordChangeController extends BaseController {

    @FXML
    TextField emailField;
    @FXML
    Button sendCodeButton;
    @FXML
    Label statusLabel;

    private final AuthService auth = BaseController.get(AuthService.class);

    /**
     * Отправляет код сброса на адрес из поля {@link #emailField}. При
     * успешной отправке открывает диалог подтверждения.
     */
    @FXML
    void sendCode() {
        statusLabel.setText("");
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError(statusLabel, "pass.status_label.enter_email");
            return;
        }

        sendCodeButton.setDisable(true);

        new Thread(() -> {
            try {
                auth.resetPasswordRequest(new PasswordResetRequest(email));
                Platform.runLater(() -> {
                    showError(statusLabel, "msg.code_sent");
                    openDialog(email);
                });
            } catch (Exception ex) {
                Platform.runLater(() -> showError(statusLabel, tr("error.base") + ex.getMessage()));
            } finally {
                Platform.runLater(() -> sendCodeButton.setDisable(false));
            }
        }).start();
    }

    /**
     * Открывает диалог ввода кода подтверждения и нового пароля.
     *
     * @param email адрес, на который был отправлен код
     */
    void openDialog(String email) {
        try {
            ResourceBundle bundle = I18n.bundle;
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/password_reset_dialog.fxml"), bundle);
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            DialogPane pane = loader.load();
            dialog.setDialogPane(pane);
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setIconified(false);
            stage.setResizable(false);

            PasswordResetDialogController ctrl = loader.getController();
            ctrl.setupValidation();
            ctrl.clearStatusLabel();
            ctrl.clearFields();
            dialog.setResultConverter(btn -> {
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return new Pair<>(ctrl.getCode(), ctrl.getPassword());
                }
                return null;
            });

            dialog.setOnCloseRequest(ev -> {
                if (dialog.getResult() == null) {
                    new Thread(() -> auth.cancelPasswordReset(email)).start();
                }
            });

            var result = dialog.showAndWait();
            if (result.isPresent()) {
                Pair<String, String> pair = result.get();
                confirmReset(email, pair.getKey(), pair.getValue());
            } else {
            }
        } catch (Exception ex) {
            showError(statusLabel, tr("error.base") + ex.getMessage());
        }
    }

    /**
     * Возвращает пользователя к экрану входа, не сохраняя введённые данные.
     */
    @FXML
    void goBack() {
        SceneManager.switchTo("authorization.fxml", c -> {
            ((BaseController) c).clearStatusLabel();
            ((BaseController) c).clearFields();
        });
    }

    /**
     * Очищает поле ввода email.
     */
    @Override
    public void clearFields() {
        if (emailField != null) emailField.clear();
    }


    /**
     * Завершает процедуру сброса пароля.
     *
     * @param email    адрес пользователя
     * @param code     код подтверждения
     * @param password новый пароль
     */
    void confirmReset(String email, String code, String password) {
        statusLabel.setText("");
        new Thread(() -> {
            try {
                auth.resetPasswordConfirm(new PasswordResetConfirm(email, code, password));
                Platform.runLater(() -> {
                    showSuccess(statusLabel, "msg.password_changed");
                    SceneManager.switchTo("authorization.fxml", c -> {
                        ((BaseController) c).clearStatusLabel();
                        ((BaseController) c).clearFields();
                    });
                });
            } catch (Exception ex) {
                new Thread(() -> auth.cancelPasswordReset(email)).start();
                Platform.runLater(() -> showError(statusLabel, tr("error.base") + ex.getMessage()));
            }
        }).start();
    }
}
