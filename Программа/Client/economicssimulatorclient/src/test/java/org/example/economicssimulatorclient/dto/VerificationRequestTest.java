package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VerificationRequestTest {

    @Test
    void testConstructorAndGetters() {
        VerificationRequest req = new VerificationRequest("mail@mail.com", "123456");
        assertEquals("mail@mail.com", req.email());
        assertEquals("123456", req.code());
    }

    @Test
    void testNullsAndEmpty() {
        assertThrows(NullPointerException.class, () -> new VerificationRequest(null, "c"));
        assertThrows(NullPointerException.class, () -> new VerificationRequest("e", null));
        assertDoesNotThrow(() -> new VerificationRequest("", ""));
    }
}
