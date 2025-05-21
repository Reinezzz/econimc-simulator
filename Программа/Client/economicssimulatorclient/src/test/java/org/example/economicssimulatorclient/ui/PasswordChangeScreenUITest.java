package org.example.economicssimulatorclient.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.MainApp;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class PasswordChangeScreenUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
        // Переход на нужный экран, если не основной
        // Например, SceneManager.switchTo("password_change.fxml");
    }

    @Test
    void testFieldsExist() {
        verifyThat("#emailField", (TextField t) -> true);
        verifyThat("#sendCodeButton", (Button t) -> true);
    }

    @Test
    void testSendCodeWithEmptyFieldShowsError() {
        clickOn("#sendCodeButton");
        verifyThat("#statusLabel", label -> !label.getText().isEmpty());
    }
}
