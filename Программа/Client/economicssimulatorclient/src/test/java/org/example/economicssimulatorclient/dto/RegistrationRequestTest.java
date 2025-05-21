package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void testConstructorAndGetters() {
        RegistrationRequest req = new RegistrationRequest("user", "mail@mail.com", "pass");
        assertEquals("user", req.username());
        assertEquals("mail@mail.com", req.email());
        assertEquals("pass", req.password());
    }

    @Test
    void testNullsAndEmpty() {
        assertThrows(NullPointerException.class, () -> new RegistrationRequest(null, "e", "p"));
        assertThrows(NullPointerException.class, () -> new RegistrationRequest("u", null, "p"));
        assertThrows(NullPointerException.class, () -> new RegistrationRequest("u", "e", null));
        assertDoesNotThrow(() -> new RegistrationRequest("", "", ""));
    }
}
