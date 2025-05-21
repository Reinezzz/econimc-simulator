package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepo;

    @Test
    void saveAndFindByUsernameAndEmail() {
        User user = User.builder()
                .username("testuser")
                .email("test@mail.com")
                .passwordHash("hash")
                .enabled(true)
                .build();
        userRepo.save(user);

        Optional<User> byUsername = userRepo.findByUsername("testuser");
        assertTrue(byUsername.isPresent());
        assertEquals("testuser", byUsername.get().getUsername());

        Optional<User> byEmail = userRepo.findByEmail("test@mail.com");
        assertTrue(byEmail.isPresent());
        assertEquals("test@mail.com", byEmail.get().getEmail());
    }

    @Test
    void existsByUsernameAndEmail() {
        User user = User.builder()
                .username("uniqueuser")
                .email("unique@mail.com")
                .passwordHash("hash")
                .enabled(true)
                .build();
        userRepo.save(user);

        assertTrue(userRepo.existsByUsername("uniqueuser"));
        assertTrue(userRepo.existsByEmail("unique@mail.com"));
        assertFalse(userRepo.existsByUsername("nouser"));
        assertFalse(userRepo.existsByEmail("no@mail.com"));
    }

    @Test
    void findByUsernameOrEmail() {
        User user = User.builder()
                .username("oruser")
                .email("or@mail.com")
                .passwordHash("hash")
                .enabled(true)
                .build();
        userRepo.save(user);

        assertTrue(userRepo.findByUsernameOrEmail("oruser", "any@mail.com").isPresent());
        assertTrue(userRepo.findByUsernameOrEmail("any", "or@mail.com").isPresent());
        assertFalse(userRepo.findByUsernameOrEmail("none", "none@mail.com").isPresent());
    }
}
