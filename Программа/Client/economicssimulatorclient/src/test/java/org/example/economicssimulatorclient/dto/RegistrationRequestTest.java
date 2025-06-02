package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RegistrationRequestTest {

    @Test
    void constructorAndGettersWork() {
        RegistrationRequest req = new RegistrationRequest("u", "e", "p");
        assertEquals("u", req.username());
        assertEquals("e", req.email());
        assertEquals("p", req.password());
    }

    @Test
    void equalsAndHashCode() {
        RegistrationRequest r1 = new RegistrationRequest("u", "e", "p");
        RegistrationRequest r2 = new RegistrationRequest("u", "e", "p");
        RegistrationRequest r3 = new RegistrationRequest("x", "e", "p");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        RegistrationRequest r = new RegistrationRequest("a", "b", "c");
        String s = r.toString();
        assertTrue(s.contains("a"));
        assertTrue(s.contains("b"));
        assertTrue(s.contains("c"));
    }
}
