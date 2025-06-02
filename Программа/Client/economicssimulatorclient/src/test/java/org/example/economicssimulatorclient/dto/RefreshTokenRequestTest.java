package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenRequestTest {

    @Test
    void constructorAndGettersWork() {
        RefreshTokenRequest req = new RefreshTokenRequest("refresh123");
        assertEquals("refresh123", req.refreshToken());
    }

    @Test
    void equalsAndHashCode() {
        RefreshTokenRequest r1 = new RefreshTokenRequest("a");
        RefreshTokenRequest r2 = new RefreshTokenRequest("a");
        RefreshTokenRequest r3 = new RefreshTokenRequest("b");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        RefreshTokenRequest r = new RefreshTokenRequest("tokenX");
        String s = r.toString();
        assertTrue(s.contains("tokenX"));
    }
}
