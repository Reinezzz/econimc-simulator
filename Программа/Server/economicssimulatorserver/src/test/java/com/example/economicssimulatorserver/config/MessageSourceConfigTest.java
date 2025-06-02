package com.example.economicssimulatorserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;

import static org.assertj.core.api.Assertions.*;

class MessageSourceConfigTest {

    @Test
    void testMessageSourceBean() {
        MessageSourceConfig config = new MessageSourceConfig();
        MessageSource source = config.messageSource();
        assertThat(source).isNotNull();
    }
}
