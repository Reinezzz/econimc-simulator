package com.example.economicssimulatorserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация кодировщика паролей.
 */
@Configuration
public class PasswordConfig {

    /**
     * Создаёт кодировщик паролей BCrypt.
     *
     * @return экземпляр {@link PasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
