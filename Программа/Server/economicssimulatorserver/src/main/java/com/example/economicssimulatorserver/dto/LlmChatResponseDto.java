package com.example.economicssimulatorserver.dto;

/**
 * Ответ от языковой модели в рамках чата.
 *
 * @param assistantMessage сообщение, сформированное моделью
 */
public record LlmChatResponseDto(
        String assistantMessage
) {}
