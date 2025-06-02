package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VerificationRequestTest {

    @Test
    void constructorAndGettersWork() {
        VerificationRequest req = new VerificationRequest("e@mail", "1234");
        assertEquals("e@mail", req.email());
        assertEquals("1234", req.code());
    }

    @Test
    void equalsAndHashCode() {
        VerificationRequest r1 = new VerificationRequest("e", "c");
        VerificationRequest r2 = new VerificationRequest("e", "c");
        VerificationRequest r3 = new VerificationRequest("x", "c");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        VerificationRequest r = new VerificationRequest("e", "c");
        String s = r.toString();
        assertTrue(s.contains("e"));
        assertTrue(s.contains("c"));
    }
}
