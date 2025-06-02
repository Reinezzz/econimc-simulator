package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordResetConfirmTest {

    @Test
    void testConstructorAndGetters() {
        PasswordResetConfirm req = new PasswordResetConfirm("mail@mail.com", "code", "pass");
        assertThat(req.email()).isEqualTo("mail@mail.com");
        assertThat(req.code()).isEqualTo("code");
        assertThat(req.newPassword()).isEqualTo("pass");
    }
}
