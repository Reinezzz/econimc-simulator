package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmChatResponseDtoTest {

    @Test
    void recordFieldsAreAccessible() {
        LlmChatResponseDto dto = new LlmChatResponseDto("Ответ ассистента");
        assertThat(dto.assistantMessage()).isEqualTo("Ответ ассистента");
    }

    @Test
    void equalsHashCodeToString() {
        LlmChatResponseDto dto1 = new LlmChatResponseDto("text");
        LlmChatResponseDto dto2 = new LlmChatResponseDto("text");

        assertThat(dto1).isEqualTo(dto2);
        assertThat(dto1.hashCode()).isEqualTo(dto2.hashCode());
        assertThat(dto1.toString()).contains("LlmChatResponseDto");
    }
}
