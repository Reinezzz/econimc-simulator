package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void constructorAndGettersWork() {
        LoginResponse resp = new LoginResponse("access", "refresh", "Bearer");
        assertEquals("access", resp.accessToken());
        assertEquals("refresh", resp.refreshToken());
        assertEquals("Bearer", resp.tokenType());
    }

    @Test
    void equalsAndHashCode() {
        LoginResponse r1 = new LoginResponse("a", "r", "t");
        LoginResponse r2 = new LoginResponse("a", "r", "t");
        LoginResponse r3 = new LoginResponse("x", "r", "t");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        LoginResponse r = new LoginResponse("a", "b", "c");
        String s = r.toString();
        assertTrue(s.contains("a"));
        assertTrue(s.contains("b"));
        assertTrue(s.contains("c"));
    }
}
