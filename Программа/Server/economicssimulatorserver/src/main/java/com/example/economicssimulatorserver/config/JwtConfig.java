package com.example.economicssimulatorserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Хранилище текущей локали приложения.
 * Используется для передачи информации о выбранном языке.
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    private String secret;

    private long accessTokenExpirationMinutes = 15;

    private String header = "Authorization";

    private String tokenPrefix = "Bearer ";
}