package com.example.economicssimulatorserver.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * Конфигурация бина для поддержки локализации сообщений приложения.
 * <p>
 * Используется для интернационализации (i18n) ошибок, статусов и любых сообщений, доступных в ресурсах {@code messages_*.properties}.
 * </p>
 */
@Configuration
public class MessageSourceConfig {
    /**
     * Создает и настраивает {@link MessageSource} для загрузки локализованных сообщений из ресурсов.
     *
     * @return бин {@link MessageSource} для поддержки i18n
     */
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }
}
