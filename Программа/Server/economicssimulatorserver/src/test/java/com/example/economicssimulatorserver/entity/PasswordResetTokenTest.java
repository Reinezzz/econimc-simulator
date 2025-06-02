package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PasswordResetTokenTest {

    @Test
    void testSettersAndGetters() {
        PasswordResetToken token = new PasswordResetToken();
        User user = new User();
        token.setId(1L);
        token.setCode("abc999");
        token.setUser(user);
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getCode()).isEqualTo("abc999");
        assertThat(token.getUser()).isEqualTo(user);
    }
}
