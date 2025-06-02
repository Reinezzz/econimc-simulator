package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save, findByToken, deleteByUser")
    void testSaveAndFindAndDelete() {
        User user = new User();
        user.setUsername("tokuser");
        user.setEmail("tokuser@test.com");
        user.setPasswordHash("hash");
        user = userRepository.save(user);

        RefreshToken token = new RefreshToken();
        token.setToken("tok123");
        token.setUser(user);
        token.setExpiryDate(Instant.now().plusSeconds(3600));
        refreshTokenRepository.save(token);

        Optional<RefreshToken> found = refreshTokenRepository.findByToken("tok123");
        assertThat(found).isPresent();
        assertThat(found.get().getUser().getUsername()).isEqualTo("tokuser");

        long count = refreshTokenRepository.deleteByUser(user);
        assertThat(count).isEqualTo(1L);
        assertThat(refreshTokenRepository.findByToken("tok123")).isNotPresent();
    }
}
