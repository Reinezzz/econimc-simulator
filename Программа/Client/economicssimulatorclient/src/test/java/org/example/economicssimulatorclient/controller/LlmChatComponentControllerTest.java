package org.example.economicssimulatorclient.controller;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.example.economicssimulatorclient.controller.LlmChatComponentController;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class LlmChatComponentControllerTest {

    LlmChatComponentController ctrl;

    @BeforeEach
    void setup() {
        ctrl = new LlmChatComponentController();
        ctrl.chatArea = new VBox();
        ctrl.inputField = new TextField();
        ctrl.sendButton = new VBox();
        ctrl.chatScroll = new ScrollPane();
    }

    @Test
    void addMessage_addsToChatArea() {
        ctrl.addMessage("user", "Hello");
        assertEquals(1, ctrl.chatArea.getChildren().size());
        HBox row = (HBox) ctrl.chatArea.getChildren().get(0);
        Label lbl = (Label) row.getChildren().get(0);
        assertEquals("Hello", lbl.getText());
    }

    @Test
    void clear_emptiesChat() {
        ctrl.addMessage("user", "Hi");
        ctrl.clear();
        assertEquals(0, ctrl.chatArea.getChildren().size());
    }

    @Test
    void onSend_ignoresEmptyInput() {
        ctrl.inputField.setText("   ");
        ctrl.setRequestSupplier(() -> null);
        int before = ctrl.chatArea.getChildren().size();
        ctrl.onSend();
        assertEquals(before, ctrl.chatArea.getChildren().size());
    }
}
