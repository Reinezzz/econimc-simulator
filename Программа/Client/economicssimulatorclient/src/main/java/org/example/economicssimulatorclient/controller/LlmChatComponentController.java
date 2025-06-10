package org.example.economicssimulatorclient.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import lombok.Getter;
import lombok.Setter;
import org.example.economicssimulatorclient.dto.LlmChatRequestDto;
import org.example.economicssimulatorclient.dto.LlmChatResponseDto;
import org.example.economicssimulatorclient.service.LlmService;
import org.example.economicssimulatorclient.util.SessionManager;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LlmChatComponentController {

    @FXML
    VBox root;
    @FXML private Label titleLabel;
    @Getter
    @FXML private VBox chatArea;
    @FXML private ScrollPane chatScroll;
    @FXML private TextField inputField;
    @FXML private VBox sendButton;

    // История сообщений (в рантайме)
    private final List<Message> chatHistory = new ArrayList<>();
    private Runnable onAssistantMessageAppended;
    // Внешний контроллер обязан передать сюда supplier с правильным LlmChatRequestDto
    @Setter
    private Supplier<LlmChatRequestDto> requestSupplier;

    public void initialize() {
        sendButton.setOnMouseClicked(event -> onSend());
        inputField.setOnAction(event -> onSend());
    }

    private void onSend() {
        String text = inputField.getText();
        if (text == null || text.trim().isEmpty() || requestSupplier == null) return;
        inputField.clear();
        addMessage("user", text);

        sendButton.setDisable(true);
        inputField.setDisable(true);

        // Получаем актуальный request с данными (параметры, визуализации и т.д.)
        LlmChatRequestDto req = requestSupplier.get();
        // Переписываем сообщение пользователя на то, что ввели сейчас
        req = new LlmChatRequestDto(
                req.modelId(),
                text,
                req.parameters(),
                req.visualizations(),
                req.result()
        );

        LlmChatRequestDto finalReq = req;
        new Thread(() -> {
            try {
                URI baseUri = SessionManager.getInstance().getBaseUri();
                LlmChatResponseDto resp = LlmService.getInstance().chat(baseUri, finalReq);
                Platform.runLater(() -> addMessage("assistant", resp.assistantMessage()));
            } catch (Exception ex) {
                Platform.runLater(() -> addMessage("system", org.example.economicssimulatorclient.util.I18n.t("error.llm") + ex.getMessage()));
            } finally {
                Platform.runLater(() -> {
                    sendButton.setDisable(false);
                    inputField.setDisable(false);
                });
            }
        }).start();
    }
    public void setOnAssistantMessageAppended(Runnable listener) {
        this.onAssistantMessageAppended = listener;
    }


    private void addMessage(String role, String message) {
        chatHistory.add(new Message(role, message));
        HBox row = new HBox();
        Label lbl = new Label(message);
        lbl.setWrapText(true);
        lbl.setMaxWidth(380);

        if ("user".equals(role)) {
            row.getStyleClass().add("llm-chat-msg-row-user");
            lbl.getStyleClass().add("llm-chat-msg-user");
        } else if ("assistant".equals(role)) {
            row.getStyleClass().add("llm-chat-msg-row-assistant");
            lbl.getStyleClass().add("llm-chat-msg-assistant");
        } else {
            row.getStyleClass().add("llm-chat-msg-row-system");
            lbl.getStyleClass().add("llm-chat-msg-system");
        }
        if ("assistant".equals(role) && onAssistantMessageAppended != null) {
            onAssistantMessageAppended.run();
        }

        row.getChildren().add(lbl);
        chatArea.getChildren().add(row);

        Platform.runLater(() -> chatScroll.setVvalue(1.0));
    }

    /** Позволяет очистить историю чата извне (например, при переключении модели) */
    public void clear() {
        chatHistory.clear();
        chatArea.getChildren().clear();
    }

    // Внутренняя структура для истории (не сериализуется)
    private static class Message {
        final String role;
        final String text;
        Message(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }
}
