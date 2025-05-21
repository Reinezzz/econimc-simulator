package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void createAndReadFields() {
        PasswordResetRequest req = new PasswordResetRequest("email@ex.com");
        assertEquals("email@ex.com", req.email());
    }
}
