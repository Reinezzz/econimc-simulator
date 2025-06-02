package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save, findByCode, findByUser, deleteByUser")
    void testSaveFindAndDelete() {
        User user = new User();
        user.setUsername("resuser");
        user.setEmail("resuser@test.com");
        user.setPasswordHash("hash");
        user = userRepository.save(user);

        PasswordResetToken token = new PasswordResetToken();
        token.setCode("reset1");
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusSeconds(10));
        passwordResetTokenRepository.save(token);

        // Проверка поиска по коду
        Optional<PasswordResetToken> foundByCode = passwordResetTokenRepository.findByCode("reset1");
        assertThat(foundByCode).isPresent();
        assertThat(foundByCode.get().getUser().getUsername()).isEqualTo("resuser");

        // Проверка поиска по пользователю
        Optional<PasswordResetToken> foundByUser = passwordResetTokenRepository.findByUser(user);
        assertThat(foundByUser).isPresent();
        assertThat(foundByUser.get().getCode()).isEqualTo("reset1");

        // Удаление по пользователю
        passwordResetTokenRepository.deleteByUser(user);
        assertThat(passwordResetTokenRepository.findByUser(user)).isNotPresent();
    }
}
