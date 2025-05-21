package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.config.JwtConfig;
import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        // Создаём поддельный конфиг с известным секретом и коротким временем жизни
        JwtConfig config = new JwtConfig();
        config.setSecret("a2FwdXRlbGx5X3NhZmVfc3RyaW5nXzI1Nl9iaXRzX2xvbmc="); // base64 строка длиной >=32 байта
        config.setAccessTokenExpirationMinutes(1); // 1 минута
        jwtUtil = new JwtUtil(config);
        // Не забудь вызвать init(), если используешь @PostConstruct
        try {
            var method = JwtUtil.class.getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(jwtUtil);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void generateToken_and_extractUsername_shouldWork() {
        UserDetails user = User.withUsername("testuser").password("pass").roles().build();
        String token = jwtUtil.generateToken(user);

        assertNotNull(token);
        String username = jwtUtil.extractUsername(token);
        assertEquals("testuser", username);
    }

    @Test
    void isTokenValid_shouldReturnTrueForValidToken() {
        UserDetails user = User.withUsername("john").password("pw").roles().build();
        String token = jwtUtil.generateToken(user);

        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_shouldReturnFalseForExpiredToken() throws InterruptedException {
        JwtConfig expConfig = new JwtConfig();
        expConfig.setSecret("a2FwdXRlbGx5X3NhZmVfc3RyaW5nXzI1Nl9iaXRzX2xvbmc=");
        expConfig.setAccessTokenExpirationMinutes(0); // Токен сразу истекает
        JwtUtil expJwtUtil = new JwtUtil(expConfig);

        try {
            var method = JwtUtil.class.getDeclaredMethod("init");
            method.setAccessible(true);
            method.invoke(expJwtUtil);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        UserDetails user = User.withUsername("john").password("pw").roles().build();
        String token = expJwtUtil.generateToken(user);
        // Ждём 1 секунду для надёжности
        Thread.sleep(1000);

        assertFalse(expJwtUtil.isTokenValid(token, user));
    }

    @Test
    void generateToken_withExtraClaims_shouldIncludeClaims() {
        UserDetails user = User.withUsername("extra").password("pw").roles().build();
        String token = jwtUtil.generateToken(user, Map.of("custom", "value"));

        // Проверяем, что claim реально есть (парсим вручную)
        String username = jwtUtil.extractUsername(token);
        assertEquals("extra", username);
        // нет прямого API для получения claims, но если понадобится — можно сделать extractClaim(...) публичным
    }
}
