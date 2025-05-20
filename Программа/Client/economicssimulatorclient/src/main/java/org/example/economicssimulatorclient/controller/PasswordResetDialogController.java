package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.economicssimulatorclient.util.I18n;

public class PasswordResetDialogController extends BaseController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField codeField;
    @FXML private PasswordField passField;
    @FXML private PasswordField repeatField;
    @FXML private Label errorLabel;
    @FXML private ButtonType okBtn;

    // initialize() можно оставить пустым!
    @FXML
    private void initialize() { }

    // Вызывать ЭТОТ МЕТОД из PasswordChangeController сразу после загрузки FXML!
    public void setupValidation() {
        // Гарантировано dialogPane уже не null!
        Button okButton = (Button) dialogPane.lookupButton(okBtn);

        Runnable validate = () -> {
            String code = codeField.getText().trim();
            String p1 = passField.getText();
            String p2 = repeatField.getText();

            boolean empty = code.isEmpty() || p1.isEmpty() || p2.isEmpty();
            boolean mismatch = !p1.equals(p2);

            if (empty) {
                errorLabel.setText("");
                okButton.setDisable(true);
            } else if (mismatch) {
                showError(errorLabel, "msg.passwords_mismatch");
                okButton.setDisable(true);
            } else {
                errorLabel.setText("");
                okButton.setDisable(false);
            }
        };

        codeField.textProperty().addListener((obs, ov, nv) -> validate.run());
        passField.textProperty().addListener((obs, ov, nv) -> validate.run());
        repeatField.textProperty().addListener((obs, ov, nv) -> validate.run());

        validate.run(); // запуск валидации сразу после загрузки
    }

    public String getCode() { return codeField.getText().trim(); }
    public String getPassword() { return passField.getText(); }
}
