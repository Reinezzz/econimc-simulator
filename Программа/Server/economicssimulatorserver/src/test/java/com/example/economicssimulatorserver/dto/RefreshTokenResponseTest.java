package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenResponseTest {

    @Test
    void testConstructorAndGetters() {
        RefreshTokenResponse resp = new RefreshTokenResponse("accessTok", "refreshTok");
        assertThat(resp.accessToken()).isEqualTo("accessTok");
        assertThat(resp.refreshToken()).isEqualTo("refreshTok");
    }
}
