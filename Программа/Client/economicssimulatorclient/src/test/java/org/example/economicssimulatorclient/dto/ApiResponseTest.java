package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void constructorAndGettersWork() {
        ApiResponse resp = new ApiResponse(true, "ok");
        assertTrue(resp.success());
        assertEquals("ok", resp.message());
    }

    @Test
    void equalsAndHashCode() {
        ApiResponse r1 = new ApiResponse(true, "msg");
        ApiResponse r2 = new ApiResponse(true, "msg");
        ApiResponse r3 = new ApiResponse(false, "other");
        assertEquals(r1, r2);
        assertNotEquals(r1, r3);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void toStringContainsFields() {
        ApiResponse r = new ApiResponse(false, "fail");
        String s = r.toString();
        assertTrue(s.contains("fail"));
        assertTrue(s.contains("false"));
    }
}
