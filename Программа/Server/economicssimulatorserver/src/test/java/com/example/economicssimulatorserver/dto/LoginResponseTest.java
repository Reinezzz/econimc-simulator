package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testConstructorAndGetters() {
        LoginResponse resp = new LoginResponse("access", "refresh", "Bearer");
        assertThat(resp.accessToken()).isEqualTo("access");
        assertThat(resp.refreshToken()).isEqualTo("refresh");
        assertThat(resp.tokenType()).isEqualTo("Bearer");
    }
}
