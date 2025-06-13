package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LlmChatResponseDtoTest {

    @Test
    void testRecordFieldsAndEquality() {
        LlmChatResponseDto dto1 = new LlmChatResponseDto("response");
        LlmChatResponseDto dto2 = new LlmChatResponseDto("response");

        assertThat(dto1.assistantMessage()).isEqualTo("response");
        assertThat(dto1).isEqualTo(dto2);
    }
}
