package org.example.economicssimulatorclient.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class PasswordResetDialogController extends BaseController {

    @FXML
    ButtonType cancelBtn;
    @FXML
    DialogPane dialogPane;
    @FXML
    TextField codeField;
    @FXML
    PasswordField passField;
    @FXML
    PasswordField repeatField;
    @FXML
    Label statusLable;
    @FXML
    ButtonType okBtn;

    @FXML
    private void initialize() {
    }

    public void setupValidation() {
        Button okButton = (Button) dialogPane.lookupButton(okBtn);
        Button cancelButton = (Button) dialogPane.lookupButton(cancelBtn);
        okButton.setId("okBtn");
        cancelButton.setId("cancelBtn");

        Runnable validate = () -> {
            String code = codeField.getText().trim();
            String p1 = passField.getText();
            String p2 = repeatField.getText();

            boolean empty = code.isEmpty() || p1.isEmpty() || p2.isEmpty();
            boolean mismatch = !p1.equals(p2);

            if (empty) {
                statusLable.setText("");
                okButton.setDisable(true);
            } else if (mismatch) {
                showError(statusLable, "msg.passwords_mismatch");
                okButton.setDisable(true);
            } else {
                statusLable.setText("");
                okButton.setDisable(false);
            }
        };

        codeField.textProperty().addListener((obs, ov, nv) -> validate.run());
        passField.textProperty().addListener((obs, ov, nv) -> validate.run());
        repeatField.textProperty().addListener((obs, ov, nv) -> validate.run());

        validate.run();
    }

    @Override
    public void clearFields() {
        if (codeField != null) codeField.clear();
        if (passField != null) passField.clear();
        if (repeatField != null) repeatField.clear();
    }

    public String getCode() {
        return codeField.getText().trim();
    }

    public String getPassword() {
        return passField.getText();
    }
}
