package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testConstructorAndGetters() {
        LoginResponse resp = new LoginResponse("access", "Bearer");
        assertEquals("access", resp.accessToken());
        assertEquals("Bearer", resp.tokenType());
    }

    @Test
    void testEqualsAndHashCode() {
        LoginResponse r1 = new LoginResponse("token", "Bearer");
        LoginResponse r2 = new LoginResponse("token", "Bearer");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }
}
