package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class VerificationCodeDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField  codeField;
    @FXML private Label      errorLabel;
    @FXML private ButtonType okBtn;            // из FXML

    private Button okButton;

    @FXML
    private void initialize() {
        // lookupButton НЕ null, т.к. ButtonTypes уже добавлены
        okButton = (Button) dialogPane.lookupButton(okBtn);
        validate();

        codeField.textProperty().addListener((o,old,v) -> validate());
    }

    private void validate() {
        boolean empty = codeField.getText().trim().isEmpty();
        okButton.setDisable(empty);
        errorLabel.setText(empty ? "Введите код" : "");
    }

    /* ---- геттер для вызывающего кода ---- */
    public String getCode() {
        return codeField.getText().trim();
    }
}
