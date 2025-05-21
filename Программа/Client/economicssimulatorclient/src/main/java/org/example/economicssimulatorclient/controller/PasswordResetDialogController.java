package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Контроллер диалога подтверждения сброса пароля (код + новый пароль).
 * Включает валидацию и методы доступа к данным.
 */
public class PasswordResetDialogController extends BaseController {

    @FXML
    DialogPane dialogPane;
    @FXML
    TextField codeField;
    @FXML
    PasswordField passField;
    @FXML
    PasswordField repeatField;
    @FXML
    Label errorLabel;
    @FXML
    ButtonType okBtn;

    /**
     * Инициализация JavaFX (по умолчанию ничего не делает).
     */
    @FXML
    private void initialize() { }

    /**
     * Включает и настраивает валидацию полей формы.
     * Должен быть вызван сразу после загрузки FXML из внешнего контроллера.
     */
    public void setupValidation() {
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

        validate.run();
    }

    /**
     * Получить введенный пользователем код.
     * @return код
     */
    public String getCode() {
        return codeField.getText().trim();
    }

    /**
     * Получить введенный пользователем новый пароль.
     * @return новый пароль
     */
    public String getPassword() {
        return passField.getText();
    }
}
