package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class VerificationRequestTest {

    @Test
    void testConstructorAndGetters() {
        VerificationRequest req = new VerificationRequest("mail@mail.com", "code123");
        assertThat(req.email()).isEqualTo("mail@mail.com");
        assertThat(req.code()).isEqualTo("code123");
    }
}
