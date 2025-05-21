package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void createAndReadFields() {
        RegistrationRequest req = new RegistrationRequest("user", "mail", "pass");
        assertEquals("user", req.username());
        assertEquals("mail", req.email());
        assertEquals("pass", req.password());
    }
}
