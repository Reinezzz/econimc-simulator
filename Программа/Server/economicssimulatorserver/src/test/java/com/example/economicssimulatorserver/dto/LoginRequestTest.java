package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testConstructorAndGetters() {
        LoginRequest req = new LoginRequest("user", "pass");
        assertThat(req.usernameOrEmail()).isEqualTo("user");
        assertThat(req.password()).isEqualTo("pass");
    }
}
