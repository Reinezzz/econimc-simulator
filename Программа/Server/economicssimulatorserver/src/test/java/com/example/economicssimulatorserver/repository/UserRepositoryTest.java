package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("save, findByEmail, findByUsername")
    void testSaveAndFind() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("mail@test.com");
        user.setPasswordHash("secret");
        userRepository.save(user);

        Optional<User> foundByEmail = userRepository.findByEmail("mail@test.com");
        assertThat(foundByEmail).isPresent();

        Optional<User> foundByUsername = userRepository.findByUsername("testuser");
        assertThat(foundByUsername).isPresent();

        assertThat(userRepository.existsByEmail("mail@test.com")).isTrue();
        assertThat(userRepository.existsByUsername("testuser")).isTrue();
    }
}
