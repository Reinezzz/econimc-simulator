package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void constructorAndGettersWork() {
        PasswordResetRequest req = new PasswordResetRequest("email@e.com");
        assertEquals("email@e.com", req.email());
    }

    @Test
    void equalsAndHashCode() {
        PasswordResetRequest r1 = new PasswordResetRequest("e");
        PasswordResetRequest r2 = new PasswordResetRequest("e");
        PasswordResetRequest r3 = new PasswordResetRequest("other");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        PasswordResetRequest r = new PasswordResetRequest("x@y");
        String s = r.toString();
        assertTrue(s.contains("x@y"));
    }
}
