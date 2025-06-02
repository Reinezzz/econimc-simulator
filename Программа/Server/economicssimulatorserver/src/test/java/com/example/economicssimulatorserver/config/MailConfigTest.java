package com.example.economicssimulatorserver.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.*;

class MailConfigTest {

    @Test
    void testJavaMailSender() {
        MailProperties props = new MailProperties();
        props.setHost("smtp.example.com");
        props.setPort(2525);
        props.setUsername("user");
        props.setPassword("pass");

        MailConfig config = new MailConfig(props);
        JavaMailSender sender = config.javaMailSender();

        assertThat(sender).isNotNull();
    }
}
