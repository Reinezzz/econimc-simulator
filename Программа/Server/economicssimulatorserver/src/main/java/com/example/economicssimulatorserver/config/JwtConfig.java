package com.example.economicssimulatorserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурация параметров для работы с JWT-токенами.
 * <p>
 * Значения параметров берутся из файла конфигурации приложения (application.properties или application.yml)
 * с префиксом {@code jwt}.
 * </p>
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * Base64-кодированный секретный ключ для подписи токенов (минимум 256 бит для HS256).
     */
    private String secret;

    /**
     * Время жизни access-токена в минутах.
     */
    private long accessTokenExpirationMinutes = 15;

    /**
     * Название HTTP-заголовка, в котором ожидается токен (обычно {@code Authorization}).
     */
    private String header = "Authorization";

    /**
     * Префикс токена в заголовке (например, {@code Bearer }).
     */
    private String tokenPrefix = "Bearer ";
}