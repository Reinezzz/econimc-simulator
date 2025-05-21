package com.example.economicssimulatorserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Конфигурация бина для кодирования паролей пользователей.
 * <p>
 * Вынесен в отдельный класс для избежания циклических зависимостей между SecurityConfig и UserService.
 * </p>
 */
@Configuration
public class PasswordConfig {

    /**
     * Создает бин {@link PasswordEncoder} на основе алгоритма BCrypt.
     *
     * @return экземпляр {@link BCryptPasswordEncoder}
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
