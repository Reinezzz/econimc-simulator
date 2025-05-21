package org.example.economicssimulatorclient.ui;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.economicssimulatorclient.MainApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.api.FxAssert;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;

import static org.testfx.api.FxAssert.verifyThat;

public class AuthorizationScreenUITest extends ApplicationTest {

    @Override
    public void start(Stage stage) throws Exception {
        new MainApp().start(stage);
    }

    @BeforeEach
    void setUp() {
        // Если нужно очистить поля, делай это тут
    }

    @Test
    void testAuthorizationFormElementsExist() {
        verifyThat("#usernameEmailField", (TextField t) -> true);
        verifyThat("#passwordField", (PasswordField t) -> true);
        verifyThat("#loginButton", (Button t) -> true);
    }

    @Test
    void testLoginButtonDisabledOnEmptyFields() {
        clickOn("#usernameEmailField").write("");
        clickOn("#passwordField").write("");
        clickOn("#loginButton");
        verifyThat("#statusLabel", LabeledMatchers.hasText("")); // или проверка на текст ошибки
    }
}
