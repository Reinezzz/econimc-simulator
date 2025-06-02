package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void testConstructorAndGetters() {
        PasswordResetRequest req = new PasswordResetRequest("mail@mail.com");
        assertThat(req.email()).isEqualTo("mail@mail.com");
    }
}
