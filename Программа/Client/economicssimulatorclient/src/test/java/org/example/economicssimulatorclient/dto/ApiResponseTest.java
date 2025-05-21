package org.example.economicssimulatorclient.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testConstructorAndGetters() {
        ApiResponse response = new ApiResponse(true, "Success message");
        assertTrue(response.success());
        assertEquals("Success message", response.message());
    }

    @Test
    void testEqualsAndHashCode() {
        ApiResponse r1 = new ApiResponse(true, "msg");
        ApiResponse r2 = new ApiResponse(true, "msg");
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());
    }

    @Test
    void testToString() {
        ApiResponse response = new ApiResponse(false, "Error!");
        assertTrue(response.toString().contains("Error!"));
    }
}
