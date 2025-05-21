package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testConstructorAndGetters() {
        LoginRequest req = new LoginRequest("user", "pass");
        assertEquals("user", req.usernameOrEmail());
        assertEquals("pass", req.password());
    }

    @Test
    void testNullAndEmptyValues() {
        assertThrows(NullPointerException.class, () -> new LoginRequest(null, "pass"));
        assertThrows(NullPointerException.class, () -> new LoginRequest("user", null));
        assertDoesNotThrow(() -> new LoginRequest("", ""));
    }
}
