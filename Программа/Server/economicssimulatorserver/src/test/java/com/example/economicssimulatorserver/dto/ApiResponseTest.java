package com.example.economicssimulatorserver.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ApiResponseTest {

    @Test
    void testConstructorAndGetters() {
        ApiResponse resp = new ApiResponse(true, "message");
        assertThat(resp.success()).isTrue();
        assertThat(resp.message()).isEqualTo("message");
    }
}
