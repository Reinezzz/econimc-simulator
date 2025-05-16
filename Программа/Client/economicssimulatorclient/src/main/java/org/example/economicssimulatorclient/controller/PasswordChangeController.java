package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;


public class PasswordChangeController {

    @FXML private TextField emailField;
    @FXML private Button    sendCodeButton;
    @FXML private Button    resetButton;
    @FXML private Label     statusLabel;

    private final AuthService auth = new AuthService();
    private boolean codeSent = false;

    /* ---------- отправка кода ---------- */
    @FXML
    private void sendCode() {
        statusLabel.setText("");
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Укажите email");
            return;
        }

        sendCodeButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.resetPasswordRequest(new PasswordResetRequest(email));
                codeSent = true;
                Platform.runLater(() -> statusLabel.setText("Код отправлен на email"));
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Ошибка: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> sendCodeButton.setDisable(false));
            }
        }).start();
    }

    @FXML
    private void reset() {
        statusLabel.setText("");
        String email = emailField.getText().trim();

        if (email.isEmpty())      { statusLabel.setText("Укажите email"); return; }
        if (!codeSent)            { statusLabel.setText("Сначала отправьте код"); return; }

        Pair<String,String> pair = showDialogAndGetData();     // открываем в FX-потоке
        if (pair == null)         { statusLabel.setText("Отменено"); return; }

        resetButton.setDisable(true);
        new Thread(() -> {
            try {
                auth.resetPasswordConfirm(
                        new PasswordResetConfirm(email, pair.getKey(), pair.getValue()));
                Platform.runLater(() -> {
                    statusLabel.setText("Пароль изменён — войдите заново");
                    SceneManager.switchTo("authorization.fxml");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> statusLabel.setText("Ошибка: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> resetButton.setDisable(false));
            }
        }).start();
    }


    @FXML
    private void goBack() { SceneManager.switchTo("authorization.fxml"); }

    private Pair<String, String> showDialogAndGetData() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/password_reset_dialog.fxml"));
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Смена пароля");
            dialog.setDialogPane(loader.load());
            //TODO: Пофиксить валидацию пароля
            //TODO: Удалять токен при закрытии диалогового окна
            PasswordResetDialogController ctrl = loader.getController();

            dialog.setResultConverter(btn -> {
                // OK: только если всё валидно
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE && ctrl.isValid()) {
                    return new Pair<>(ctrl.getCode(), ctrl.getPassword());
                }
                // Отмена или закрытие — вернёт null
                return null;
            });

            return dialog.showAndWait().orElse(null);

        } catch (Exception e) {
            statusLabel.setText("Ошибка диалога");
            return null;
        }
    }

}
