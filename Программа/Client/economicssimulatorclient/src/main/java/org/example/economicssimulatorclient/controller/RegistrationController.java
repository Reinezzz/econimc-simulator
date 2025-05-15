package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.SceneManager;

import java.io.IOException;
import java.util.Optional;

public class RegistrationController {

    @FXML private TextField     usernameField;
    @FXML private TextField     emailField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private Button        registerButton;
    @FXML private Label         statusLabel;

    private final AuthService auth = new AuthService();

    /* ---------------- регистрация ---------------- */
    @FXML
    private void doRegister() {
        statusLabel.setText("");

        String user  = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String p1    = passwordField.getText();
        String p2    = repeatPasswordField.getText();

        if (user.isEmpty() || email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            statusLabel.setText("Заполните все поля");    return;
        }
        if (!p1.equals(p2)) {
            statusLabel.setText("Пароли не совпадают");   return;
        }

        registerButton.setDisable(true);

        runAsync(() -> {   // шаг-1: сеть, не блокируем UI
            try {
                auth.register(new RegistrationRequest(user, email, p1));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {   // шаг-2: вернулись в FX-поток
                statusLabel.setText("Код отправлен на email");

                String code = showVerificationDialog();   // теперь можно!
                if (code == null) {
                    statusLabel.setText("Ввод кода отменён");
                    registerButton.setDisable(false);
                    return;
                }

                // шаг-3: подтверждение снова в фоне
                runAsync(() -> {
                    try {
                        auth.verifyEmail(new VerificationRequest(email, code));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        statusLabel.setText("Успешно! Войдите.");
                        SceneManager.switchTo("authorization.fxml");
                    });
                });
            });
        }, ex -> Platform.runLater(() -> {
            statusLabel.setText("Ошибка: " + ex.getMessage());
            registerButton.setDisable(false);
        }));
    }

    /* ==== маленькая утилита ==== */
    private void runAsync(Runnable task) { runAsync(task, null); }
    private void runAsync(Runnable task, java.util.function.Consumer<Throwable> onError) {
        Thread t = new Thread(() -> {
            try { task.run(); }
            catch (Throwable ex) { if (onError != null) onError.accept(ex); }
        }, "fx-bg");
        t.setDaemon(true);
        t.start();
    }


    /* ---------------- откр. логин ---------------- */
    @FXML
    private void openLogin() {
        SceneManager.switchTo("authorization.fxml");
    }

    /* =========== helper: диалог кода ============ */
    private String showVerificationDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/verification_code_dialog.fxml"));

            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Подтверждение e-mail");
            dialog.setDialogPane(loader.load());

            VerificationCodeDialogController ctrl = loader.getController();

            dialog.setResultConverter(btn -> {
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return ctrl.getCode();          // читаем из контроллера
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);

        } catch (Exception ex) {
            Platform.runLater(() -> statusLabel.setText("Не удалось открыть диалог"));
            System.out.println(ex.getMessage());
            return null;
        }
    }


}
