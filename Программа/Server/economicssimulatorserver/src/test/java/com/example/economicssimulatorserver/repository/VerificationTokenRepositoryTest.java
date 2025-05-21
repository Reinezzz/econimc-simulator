package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.entity.VerificationToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VerificationTokenRepositoryTest {

    @Autowired
    private VerificationTokenRepository verificationRepo;
    @Autowired
    private UserRepository userRepo;

    @Test
    void saveAndFindAndDeleteByUser() {
        User user = User.builder()
                .username("verifyuser")
                .email("verify@mail.com")
                .passwordHash("hash")
                .enabled(true)
                .build();
        userRepo.save(user);

        VerificationToken token = VerificationToken.builder()
                .user(user)
                .code("111222")
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationRepo.save(token);

        assertTrue(verificationRepo.findByCode("111222").isPresent());
        assertTrue(verificationRepo.findByUser(user).isPresent());

        verificationRepo.deleteByUser(user);
        assertFalse(verificationRepo.findByUser(user).isPresent());
    }
}
