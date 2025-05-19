package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.example.economicssimulatorclient.util.I18n;

public class VerificationCodeDialogController {

    @FXML private DialogPane dialogPane;
    @FXML private TextField  codeField;
    @FXML private Label      errorLabel;
    @FXML private ButtonType okBtn;
    @FXML private ButtonType cancelBtn;   // получаем из FXML

    @FXML
    private void initialize() {
        /* lookupButton может быть ещё null внутри initialize,
           поэтому откладываем до первого «pulse» JavaFX */
        Platform.runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(okBtn);
            okButton.setDisable(true);
            Button cancelButton = (Button) dialogPane.lookupButton(cancelBtn);

            codeField.textProperty().addListener((obs, o, n) -> {
                boolean empty = n.trim().isEmpty();
                okButton.setDisable(empty);
                cancelButton.setDisable(empty);
                errorLabel.setText(empty ? I18n.t("dialog.status_label.code_required") : "");
            });
        });
    }

    /* для вызова из регистрационного контроллера */
    public String getCode() {
        return codeField.getText().trim();
    }
}
