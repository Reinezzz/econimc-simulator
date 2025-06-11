package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.dto.*;
import org.example.economicssimulatorclient.service.AuthService;
import org.example.economicssimulatorclient.util.I18n;
import org.example.economicssimulatorclient.util.SceneManager;
import org.example.economicssimulatorclient.util.Validator;

import java.io.IOException;
import java.util.ResourceBundle;

public class RegistrationController extends BaseController {

    @FXML
    TextField usernameField;
    @FXML
    TextField emailField;
    @FXML
    PasswordField passwordField;
    @FXML
    PasswordField repeatPasswordField;
    @FXML
    Button registerButton;
    @FXML
    Label statusLabel;

    private final AuthService auth = BaseController.get(AuthService.class);

    @FXML
    void doRegister() {
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

        runAsync(() -> {
            try {
                auth.register(new RegistrationRequest(user, email, p1));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            Platform.runLater(() -> {
                showSuccess(statusLabel, "msg.code_sent");
                String code = showVerificationDialog();
                if (code == null) {
                    showError(statusLabel, "reg.status_label.cancel_verification");
                    auth.cancelRegistration(email);
                    registerButton.setDisable(false);
                    return;
                }
                runAsync(() -> {
                    try {
                        auth.verifyEmail(new VerificationRequest(email, code));
                    } catch (IOException | InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        showSuccess(statusLabel, "msg.registration_success");
                        SceneManager.switchToWithoutContorller("main.fxml");
                    });
                }, ex -> Platform.runLater(() -> {
                    showError(statusLabel, tr("error.base") + ex.getMessage());
                    registerButton.setDisable(false);
                }));
            });
        }, ex -> Platform.runLater(() -> {
            showError(statusLabel, tr("error.base") + ex.getMessage());
            registerButton.setDisable(false);
        }));
    }

    @FXML
    void openLogin() {
        SceneManager.switchTo("authorization.fxml", c -> {
            ((BaseController) c).clearStatusLabel();
            ((BaseController) c).clearFields();
        });
    }

    @Override
    public void clearFields() {
        if (usernameField != null) usernameField.clear();
        if (emailField != null) emailField.clear();
        if (passwordField != null) passwordField.clear();
        if (repeatPasswordField != null) repeatPasswordField.clear();
    }

    String showVerificationDialog() {
        try {
            ResourceBundle bundle = I18n.bundle;
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/org/example/economicssimulatorclient/verification_code_dialog.fxml"), bundle);
            Dialog<String> dialog = new Dialog<>();
            dialog.setDialogPane(loader.load());
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.setIconified(false);
            stage.setResizable(false);

            VerificationCodeDialogController ctrl = loader.getController();
            ctrl.clearStatusLabel();
            ctrl.clearFields();
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
            showError(statusLabel, tr("error.base") + ex.getMessage());
            return null;
        }
    }
}
