package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenRequestTest {

    @Test
    void testConstructorAndGetters() {
        RefreshTokenRequest req = new RefreshTokenRequest("refreshTok");
        assertThat(req.refreshToken()).isEqualTo("refreshTok");
    }
}
