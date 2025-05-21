package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void createAndReadFields() {
        LoginRequest req = new LoginRequest("user", "password");
        assertEquals("user", req.usernameOrEmail());
        assertEquals("password", req.password());
    }
}
