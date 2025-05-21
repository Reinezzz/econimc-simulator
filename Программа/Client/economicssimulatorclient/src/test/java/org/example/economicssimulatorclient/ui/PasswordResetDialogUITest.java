package org.example.economicssimulatorclient.ui;

import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.MainApp;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class PasswordResetDialogUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
        // Можно смоделировать открытие диалога
        // Или вызвать диалог через контроллер
    }

    @Test
    void testDialogFieldsExist() {
        verifyThat("#codeField", (TextField t) -> true);
        verifyThat("#passField", (TextField t) -> true);
        verifyThat("#repeatField", (TextField t) -> true);
        verifyThat("#errorLabel", (label) -> true);
    }
}
