package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.util.Pair;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;
import org.example.economicssimulatorclient.util.Validator;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;

public class RegistrationController extends BaseController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private Button registerButton;
    @FXML
    private Label statusLabel;

    private final AuthService auth = new AuthService();

    /* ---------------- регистрация ---------------- */
    @FXML
    private void doRegister() {
        statusLabel.setText("");

        String user = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String p1 = passwordField.getText();
        String p2 = repeatPasswordField.getText();

        if (!Validator.isValidEmail(email)) {
            showError(statusLabel, "msg.invalid_email");
            return;
        }
        if (!Validator.isStrongPassword(p1)) {
            showError(statusLabel, "msg.weak_password");
            return;
        }

        if (user.isEmpty() || email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            showError(statusLabel, "msg.fill_all_fields");
            return;
        }
        if (!p1.equals(p2)) {
            showError(statusLabel, "msg.passwords_mismatch");
            return;
        }

        registerButton.setDisable(true);

        runAsync(() -> {                                     // фоновая сеть
            try {
                auth.register(new RegistrationRequest(user, email, p1));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {                        // диалог — FX-поток
                showSuccess(statusLabel, "msg.code_sent");
                String code = showVerificationDialog();      // блокирует до OK/Cancel
                if (code == null) {
                    showError(statusLabel, "reg.status_label.cancel_verification");
                    auth.cancelRegistration(email);
                    registerButton.setDisable(false);
                    return;
                }

                // второе сетевое обращение в фоне
                runAsync(() -> {
                    try {
                        auth.verifyEmail(new VerificationRequest(email, code));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        showSuccess(statusLabel, "msg.registration_success");
                        SceneManager.switchTo("authorization.fxml");
                    });
                }, ex -> Platform.runLater(() -> {
                    showError(statusLabel, tr("error.base" + ex.getMessage()));
                    registerButton.setDisable(false);
                }));
            });

        }, ex -> Platform.runLater(() -> {
            showError(statusLabel, tr("error.base" + ex.getMessage()));
            registerButton.setDisable(false);
        }));
    }

    /* ==== маленькая утилита ==== */
    /* ---------- helper ---------- */
    private void runAsync(Runnable task, java.util.function.Consumer<Throwable> onErr) {
        Thread t = new Thread(() -> {
            try {
                task.run();
            } catch (Throwable ex) {
                if (onErr != null) onErr.accept(ex);
            }
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
            ResourceBundle bundle = I18n.bundle;
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/verification_code_dialog.fxml"), bundle);
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle(I18n.t("dialog.verification_code_title"));
            dialog.setDialogPane(loader.load());

            VerificationCodeDialogController ctrl = loader.getController();

            dialog.setResultConverter(btn -> {
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    return ctrl.getCode();
                if (btn != null && btn.getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
                    auth.cancelRegistration(emailField.getText());
                    return null;
                }
                return null;
            });

            return dialog.showAndWait().orElse(null);

        } catch (Exception ex) {
            statusLabel.setText(I18n.t("error.base") + ex.getMessage());
            return null;
        }
    }



}
