package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void testConstructorAndGetters() {
        PasswordResetRequest req = new PasswordResetRequest("mail@mail.com");
        assertEquals("mail@mail.com", req.email());
    }

    @Test
    void testNullsAndEmpty() {
        assertThrows(NullPointerException.class, () -> new PasswordResetRequest(null));
        assertDoesNotThrow(() -> new PasswordResetRequest(""));
    }
}
