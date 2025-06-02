package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class RefreshTokenTest {

    @Test
    void testSettersAndGetters() {
        RefreshToken token = new RefreshToken();
        User user = new User();
        token.setId(1L);
        token.setToken("refreshTok");
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(60));
        assertThat(token.getId()).isEqualTo(1L);
        assertThat(token.getToken()).isEqualTo("refreshTok");
        assertThat(token.getUser()).isEqualTo(user);
        assertThat(token.getExpiryDate()).isAfter(Instant.now());
    }
}
