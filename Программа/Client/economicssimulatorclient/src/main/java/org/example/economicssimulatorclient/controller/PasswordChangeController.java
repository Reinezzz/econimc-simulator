package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import org.example.economicssimulatorclient.dto.PasswordResetConfirm;
import org.example.economicssimulatorclient.dto.PasswordResetRequest;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;
import javafx.util.Pair;

public class PasswordChangeController {

    @FXML private TextField emailField;
    @FXML private Button sendCodeButton;
    @FXML private Label statusLabel;

    private final AuthService auth = new AuthService();

    @FXML
    private void sendCode() {
        statusLabel.setText("");
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText(I18n.t("pass.status_label.enter_email"));
            return;
        }

        sendCodeButton.setDisable(true);

        new Thread(() -> {
            try {
                auth.resetPasswordRequest(new PasswordResetRequest(email));
                Platform.runLater(() -> {
                    statusLabel.setText(I18n.t("msg.code_sent"));
                    openDialog(email); // Сразу открываем окно
                });
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText(I18n.t("error.base") + ex.getMessage()));
            } finally {
                Platform.runLater(() -> sendCodeButton.setDisable(false));
            }
        }).start();
    }

    // Открытие диалога
    private void openDialog(String email) {
        int[] attempts = {0};
        boolean[] confirmed = {false};

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/password_reset_dialog.fxml"));
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle(I18n.t("dialog.password_reset_title"));
            DialogPane pane = loader.load();
            dialog.setDialogPane(pane);

            PasswordResetDialogController ctrl = loader.getController();
            ctrl.setupValidation();

            // Обработка подтверждения
            dialog.setResultConverter(btn -> {
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return new Pair<>(ctrl.getCode(), ctrl.getPassword());
                }
                return null;
            });

            // При закрытии/отмене — удаляем токен на сервере
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
                // После отмены ничего не делаем (или можно вернуть на экран авторизации)
            }

        } catch (Exception e) {
            statusLabel.setText(I18n.t("error.base") + e.getMessage());
        }
    }

    @FXML
    private void goBack() { SceneManager.switchTo("authorization.fxml"); }

    // Смена пароля на сервере
    private void confirmReset(String email, String code, String password) {
        statusLabel.setText("");
        new Thread(() -> {
            try {
                auth.resetPasswordConfirm(new PasswordResetConfirm(email, code, password));
                Platform.runLater(() -> {
                    statusLabel.setText(I18n.t("msg.password_changed"));
                    SceneManager.switchTo("authorization.fxml");
                });
            } catch (Exception ex) {
                new Thread(() -> auth.cancelPasswordReset(email)).start();
                Platform.runLater(() -> statusLabel.setText(I18n.t("error.base") + ex.getMessage()));
            }
        }).start();
    }
}
