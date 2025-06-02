package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenResponseTest {

    @Test
    void constructorAndGettersWork() {
        RefreshTokenResponse resp = new RefreshTokenResponse("a", "b");
        assertEquals("a", resp.accessToken());
        assertEquals("b", resp.refreshToken());
    }

    @Test
    void equalsAndHashCode() {
        RefreshTokenResponse r1 = new RefreshTokenResponse("a", "b");
        RefreshTokenResponse r2 = new RefreshTokenResponse("a", "b");
        RefreshTokenResponse r3 = new RefreshTokenResponse("a", "x");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        RefreshTokenResponse r = new RefreshTokenResponse("t1", "t2");
        String s = r.toString();
        assertTrue(s.contains("t1"));
        assertTrue(s.contains("t2"));
    }
}
