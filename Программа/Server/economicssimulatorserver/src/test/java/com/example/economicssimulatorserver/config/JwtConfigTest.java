package com.example.economicssimulatorserver.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JwtConfigTest {

    @Test
    void testDefaultAndCustomValues() {
        JwtConfig config = new JwtConfig();
        config.setSecret("mySecret");
        config.setAccessTokenExpirationMinutes(22);
        config.setHeader("X-MyAuth");
        config.setTokenPrefix("MyBearer ");

        assertThat(config.getSecret()).isEqualTo("mySecret");
        assertThat(config.getAccessTokenExpirationMinutes()).isEqualTo(22L);
        assertThat(config.getHeader()).isEqualTo("X-MyAuth");
        assertThat(config.getTokenPrefix()).isEqualTo("MyBearer ");
    }
}
