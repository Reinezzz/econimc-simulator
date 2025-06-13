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

/**
 * Контроллер переиспользуемого компонента LLM-чата. Отвечает за отправку
 * сообщений пользователя, получение ответов ассистента и прокрутку списка.
 */
public class LlmChatComponentController {

    @FXML
    VBox root;
    @FXML
    private Label titleLabel;
    @Getter
    @FXML
    private VBox chatArea;
    @FXML
    private ScrollPane chatScroll;
    @FXML
    private TextField inputField;
    @FXML
    private VBox sendButton;

    private final List<Message> chatHistory = new ArrayList<>();
    @Setter
    private Runnable onAssistantMessageAppended;
    @Setter
    private Supplier<LlmChatRequestDto> requestSupplier;

    /**
     * Инициализирует обработчики: связывает кнопку отправки и клавишу Enter
     * с методом {@link #onSend()}.
     */
    public void initialize() {
        sendButton.setOnMouseClicked(event -> onSend());
        inputField.setOnAction(event -> onSend());
    }

    /**
     * Отправляет введённое сообщение и обрабатывает ответ ассистента.
     * Блокирует элементы управления на время запроса.
     */
    private void onSend() {
        String text = inputField.getText();
        if (text == null || text.trim().isEmpty() || requestSupplier == null) return;
        inputField.clear();
        addMessage("user", text);

        sendButton.setDisable(true);
        inputField.setDisable(true);

        LlmChatRequestDto req = requestSupplier.get();
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

    /**
     * Добавляет сообщение в историю и отображает его в области чата.
     *
     * @param role    роль отправителя (user/assistant/system)
     * @param message текст сообщения
     */
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

    /**
     * Очищает историю сообщений и область отображения чата.
     */
    public void clear() {
        chatHistory.clear();
        chatArea.getChildren().clear();
    }

    /**
     * Представляет одно сообщение в истории чата.
     */
    private static class Message {
        final String role;
        final String text;

        /**
         * Создаёт сообщение указанного типа.
         *
         * @param role роль автора
         * @param text текст сообщения
         */
        Message(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }
}
