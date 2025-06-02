package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void constructorAndGettersWork() {
        LoginRequest req = new LoginRequest("user", "pass");
        assertEquals("user", req.usernameOrEmail());
        assertEquals("pass", req.password());
    }

    @Test
    void equalsAndHashCode() {
        LoginRequest r1 = new LoginRequest("u", "p");
        LoginRequest r2 = new LoginRequest("u", "p");
        LoginRequest r3 = new LoginRequest("other", "p");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        LoginRequest r = new LoginRequest("u", "p");
        String s = r.toString();
        assertTrue(s.contains("u"));
        assertTrue(s.contains("p"));
    }
}
