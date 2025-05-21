package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VerificationRequestTest {

    @Test
    void createAndReadFields() {
        VerificationRequest req = new VerificationRequest("mail", "123456");
        assertEquals("mail", req.email());
        assertEquals("123456", req.code());
    }
}
