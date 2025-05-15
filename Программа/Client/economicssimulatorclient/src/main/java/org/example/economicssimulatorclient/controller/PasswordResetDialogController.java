package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

public class PasswordResetDialogController {

    @FXML private DialogPane    dialogPane;
    @FXML private TextField     codeField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private Label         errorLabel;

    private Button okButton;

    @FXML
    private void initialize() {
        okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
        validate();

        codeField.textProperty().addListener((o,old,v)-> validate());
        passwordField.textProperty().addListener((o,old,v)-> validate());
        repeatPasswordField.textProperty().addListener((o,old,v)-> validate());
    }

    private void validate() {
        boolean empty = codeField.getText().trim().isEmpty()
                || passwordField.getText().isEmpty()
                || repeatPasswordField.getText().isEmpty();
        boolean mismatch = !passwordField.getText().equals(repeatPasswordField.getText());

        if (empty) {
            errorLabel.setText("");
            okButton.setDisable(true);
        } else if (mismatch) {
            errorLabel.setText("Пароли не совпадают");
            okButton.setDisable(true);
        } else {
            errorLabel.setText("");
            okButton.setDisable(false);
        }
    }

    /* ==== геттеры для вызывающего кода ==== */
    public String getCode()      { return codeField.getText().trim(); }
    public String getPassword()  { return passwordField.getText();    }
}
