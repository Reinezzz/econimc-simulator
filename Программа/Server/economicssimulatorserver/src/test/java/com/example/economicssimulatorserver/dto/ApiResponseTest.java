package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    void createAndReadFields() {
        ApiResponse resp = new ApiResponse(true, "success!");
        assertTrue(resp.success());
        assertEquals("success!", resp.message());

        ApiResponse fail = new ApiResponse(false, "error");
        assertFalse(fail.success());
        assertEquals("error", fail.message());
    }
}
