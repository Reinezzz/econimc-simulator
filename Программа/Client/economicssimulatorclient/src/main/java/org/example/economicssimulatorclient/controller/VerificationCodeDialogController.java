package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Контроллер диалога ввода кода подтверждения при регистрации.
 * Проверяет введённый код и включает или отключает кнопки диалога.
 */
public class VerificationCodeDialogController extends BaseController {

    @FXML
    DialogPane dialogPane;
    @FXML
    TextField codeField;
    @FXML
    Label statusLable;
    @FXML
    ButtonType okBtn;
    @FXML
    ButtonType cancelBtn;

    /**
     * Настраивает слушатели, проверяющие введённый код и управляющие
     * доступностью кнопок диалога.
     */
    @FXML
    void initialize() {
        Platform.runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(okBtn);
            okButton.setDisable(true);
            Button cancelButton = (Button) dialogPane.lookupButton(cancelBtn);
            okButton.setId("okBtn");
            cancelButton.setId("cancelBtn");

            codeField.textProperty().addListener((obs, o, n) -> {
                boolean empty = n.trim().isEmpty();
                okButton.setDisable(empty);
                cancelButton.setDisable(empty);
                statusLable.setText(empty ? tr("dialog.status_label.code_required") : "");
            });
        });
    }

    /**
     * Очищает поле с кодом.
     */
    @Override
    public void clearFields() {
        if (codeField != null) codeField.clear();
    }

    /**
     * Возвращает код подтверждения, введённый пользователем.
     *
     * @return строка с кодом
     */
    public String getCode() {
        return codeField.getText().trim();
    }
}
