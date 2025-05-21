package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ApiResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {

    @Test
    void toJsonAndFromJson() throws Exception {
        ApiResponse response = new ApiResponse(true, "success");
        String json = JsonUtil.toJson(response);
        assertTrue(json.contains("success"));

        ApiResponse parsed = JsonUtil.fromJson(json, ApiResponse.class);
        assertEquals(response.success(), parsed.success());
        assertEquals(response.message(), parsed.message());
    }

    @Test
    void fromJsonWithInvalidJsonThrows() {
        assertThrows(Exception.class, () -> {
            JsonUtil.fromJson("not-a-json", ApiResponse.class);
        });
    }
}
