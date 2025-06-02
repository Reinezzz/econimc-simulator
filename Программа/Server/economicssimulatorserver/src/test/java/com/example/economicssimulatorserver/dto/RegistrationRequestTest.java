package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void testConstructorAndGetters() {
        RegistrationRequest req = new RegistrationRequest("user", "mail@mail.com", "pass");
        assertThat(req.username()).isEqualTo("user");
        assertThat(req.email()).isEqualTo("mail@mail.com");
        assertThat(req.password()).isEqualTo("pass");
    }
}
