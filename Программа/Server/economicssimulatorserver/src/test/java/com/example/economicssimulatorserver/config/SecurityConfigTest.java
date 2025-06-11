package com.example.economicssimulatorserver.config;

import com.example.economicssimulatorserver.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.*;

class SecurityConfigTest {

    @Test
    void testConstruct() {
        JwtUtil jwtUtil = Mockito.mock(JwtUtil.class);
        JwtConfig jwtConfig = new JwtConfig();
        UserDetailsService uds = Mockito.mock(UserDetailsService.class);

        SecurityConfig config = new SecurityConfig(jwtUtil, jwtConfig, uds);
        assertThat(config).isNotNull();
    }
}
