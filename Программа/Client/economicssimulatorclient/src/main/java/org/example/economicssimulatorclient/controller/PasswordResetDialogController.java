package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordResetDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField codeField;
    @FXML private PasswordField passField;
    @FXML private PasswordField repeatField;
    @FXML private Label errorLabel;
    @FXML private ButtonType okBtn;

    private boolean valid = false;

    @FXML
    private void initialize() {
        // Сохраняем оригинальный обработчик
        Button okButton = (Button) dialogPane.lookupButton(okBtn);
        okButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!validateAndShowError()) {
                event.consume(); // блокируем закрытие окна!
            } else {
                valid = true;
            }
        });
    }

    /**
     * Валидирует поля и показывает ошибку, если что-то не так.
     * @return true если поля валидны, иначе false.
     */
    private boolean validateAndShowError() {
        String code = codeField.getText().trim();
        String p1 = passField.getText();
        String p2 = repeatField.getText();

        if (code.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            errorLabel.setText("Все поля обязательны");
            return false;
        }
        if (!p1.equals(p2)) {
            errorLabel.setText("Пароли не совпадают");
            return false;
        }
        errorLabel.setText("");
        return true;
    }

    public boolean isValid() { return valid; }
    public String getCode() { return codeField.getText().trim(); }
    public String getPassword() { return passField.getText(); }
}
