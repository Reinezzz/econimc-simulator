package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenTest {

    @Test
    void testSettersAndGetters() {
        VerificationToken token = new VerificationToken();
        User user = new User();
        token.setId(1L);
        token.setCode("abc123");
        token.setUser(user);
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getCode()).isEqualTo("abc123");
        assertThat(token.getUser()).isEqualTo(user);
    }
}
