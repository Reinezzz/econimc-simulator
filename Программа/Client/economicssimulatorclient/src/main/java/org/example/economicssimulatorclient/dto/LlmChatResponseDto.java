package org.example.economicssimulatorclient.dto;

/**
 * Ответ языковой модели на сообщение пользователя.
 *
 * @param assistantMessage текст сообщения от ИИ
 */
public record LlmChatResponseDto(
        String assistantMessage
) {}
