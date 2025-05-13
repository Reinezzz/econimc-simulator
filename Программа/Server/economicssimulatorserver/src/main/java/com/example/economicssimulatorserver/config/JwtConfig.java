package com.example.economicssimulatorserver.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Настройки JWT считываются из application.yml (prefix = jwt).
 */
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /** base64‑encoded секретный ключ (≥ 256‑бит для HS256) */
    private String secret;
    /** время жизни access‑token, минут */
    private long accessTokenExpirationMinutes = 15;
    /** HTTP‑заголовок, в котором ищем токен (Authorization) */
    private String header = "Authorization";
    /** Префикс (Bearer ) */
    private String tokenPrefix = "Bearer ";
}
