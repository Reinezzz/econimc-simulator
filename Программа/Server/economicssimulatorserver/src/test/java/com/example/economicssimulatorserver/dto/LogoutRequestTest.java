package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LogoutRequestTest {

    @Test
    void testConstructorAndGetters() {
        LogoutRequest req = new LogoutRequest("refreshToken");
        assertThat(req.refreshToken()).isEqualTo("refreshToken");
    }
}
