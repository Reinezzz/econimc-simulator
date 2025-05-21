package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PasswordResetTokenRepositoryTest {

    @Autowired
    private PasswordResetTokenRepository resetRepo;
    @Autowired
    private UserRepository userRepo;

    @Test
    void saveAndFindAndDeleteByUser() {
        User user = User.builder()
                .username("resetuser")
                .email("reset@mail.com")
                .passwordHash("hash")
                .enabled(true)
                .build();
        userRepo.save(user);

        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .code("654321")
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .build();
        resetRepo.save(token);

        assertTrue(resetRepo.findByCode("654321").isPresent());
        assertTrue(resetRepo.findByUser(user).isPresent());

        resetRepo.deleteByUser(user);
        assertFalse(resetRepo.findByUser(user).isPresent());
    }
}
