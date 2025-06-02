package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.entity.VerificationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save, findByCode, findByUser, deleteByUser")
    void testSaveFindAndDelete() {
        User user = new User();
        user.setUsername("veruser");
        user.setEmail("veruser@test.com");
        user.setPasswordHash("hash");
        user = userRepository.save(user);

        VerificationToken token = new VerificationToken();
        token.setCode("verif1");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(10));
        verificationTokenRepository.save(token);

        // Проверка поиска по коду
        Optional<VerificationToken> foundByCode = verificationTokenRepository.findByCode("verif1");
        assertThat(foundByCode).isPresent();
        assertThat(foundByCode.get().getUser().getUsername()).isEqualTo("veruser");

        // Проверка поиска по пользователю
        Optional<VerificationToken> foundByUser = verificationTokenRepository.findByUser(user);
        assertThat(foundByUser).isPresent();
        assertThat(foundByUser.get().getCode()).isEqualTo("verif1");

        // Удаление по пользователю
        verificationTokenRepository.deleteByUser(user);
        assertThat(verificationTokenRepository.findByUser(user)).isNotPresent();
    }
}
