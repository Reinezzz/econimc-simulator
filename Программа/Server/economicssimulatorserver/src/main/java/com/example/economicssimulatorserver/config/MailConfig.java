package com.example.economicssimulatorserver.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Конфигурация почтового клиента.
 * Используется для отправки писем через SMTP.
 */
@Configuration
@EnableConfigurationProperties(MailProperties.class)
@RequiredArgsConstructor
public class MailConfig {

    private final MailProperties mailProperties;

    /**
     * Создаёт отправитель почты с настройками из {@link MailProperties}.
     *
     * @return экземпляр {@link JavaMailSender}
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(mailProperties.getHost());
        sender.setPort(mailProperties.getPort());
        sender.setUsername(mailProperties.getUsername());
        sender.setPassword(mailProperties.getPassword());
        sender.setDefaultEncoding(mailProperties.getDefaultEncoding().name());

        Properties props = sender.getJavaMailProperties();
        props.putAll(mailProperties.getProperties());

        return sender;
    }
}
