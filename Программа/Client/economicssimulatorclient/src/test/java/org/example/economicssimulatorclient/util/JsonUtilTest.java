package org.example.economicssimulatorclient.util;

import org.example.economicssimulatorclient.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JsonUtilTest {

    @Test
    void toJsonAndFromJsonRoundtrip() throws Exception {
        ApiResponse resp = new ApiResponse(true, "message");
        String json = JsonUtil.toJson(resp);
        assertNotNull(json);
        assertTrue(json.contains("message"));

        ApiResponse resp2 = JsonUtil.fromJson(json, ApiResponse.class);
        assertEquals(resp, resp2);
    }
}
