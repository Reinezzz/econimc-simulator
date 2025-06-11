package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setup() {
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("c2VjcmV0c2VjcmV0c2VjcmV0c2VjcmV0MTIzNDU2Nzg5MA==");
        jwtConfig.setAccessTokenExpirationMinutes(10L);
        this.jwtUtil = new JwtUtil(jwtConfig);
        jwtUtil.init();
    }

    @Test
    void testGenerateAndValidateToken() {
        User user = new User("testuser", "pass", java.util.List.of());
        String token = jwtUtil.generateToken(user);
        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("testuser");
    }

    @Test
    void testExtraClaims() {
        User user = new User("john", "pass", java.util.List.of());
        String token = jwtUtil.generateToken(user, Map.of("role", "ADMIN"));
        assertThat(jwtUtil.isTokenValid(token, user)).isTrue();
    }
}
