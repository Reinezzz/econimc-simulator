package org.example.economicssimulatorclient.ui;

import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.MainApp;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class RegistrationScreenUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
        // Переход на регистрацию если нужно
        // SceneManager.switchTo("registration.fxml");
    }

    @Test
    void testFieldsExist() {
        verifyThat("#usernameField", (TextField t) -> true);
        verifyThat("#emailField", (TextField t) -> true);
        verifyThat("#passwordField", (TextField t) -> true);
        verifyThat("#repeatPasswordField", (TextField t) -> true);
        verifyThat("#registerButton", (Button t) -> true);
    }

//    @Test
//    void testRegistrationValidation() {
//        clickOn("#registerButton");
//        verifyThat("#statusLabel", label -> !label.getText().isEmpty());
//    }
}
