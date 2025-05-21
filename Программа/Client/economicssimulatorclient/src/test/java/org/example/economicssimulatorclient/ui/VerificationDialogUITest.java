package org.example.economicssimulatorclient.ui;

import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.MainApp;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class VerificationDialogUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
        // Открытие окна диалога - если не основной, вызвать руками через контроллер
    }

    @Test
    void testFieldsExist() {
        verifyThat("#codeField", (TextField t) -> true);
        verifyThat("#errorLabel", (label) -> true);
    }
}
