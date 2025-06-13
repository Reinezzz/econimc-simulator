package com.example.economicssimulatorserver.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void builderAndAllArgsConstructorAndSetters() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        User user = User.builder()
                .id(5L)
                .username("vasya")
                .email("vasya@site.com")
                .passwordHash("hash")
                .enabled(true)
                .createdAt(created)
                .updatedAt(updated)
                .build();

        assertThat(user.getId()).isEqualTo(5L);
        assertThat(user.getUsername()).isEqualTo("vasya");
        assertThat(user.getEmail()).isEqualTo("vasya@site.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.isEnabled()).isTrue();
        assertThat(user.getCreatedAt()).isEqualTo(created);
        assertThat(user.getUpdatedAt()).isEqualTo(updated);
    }
}
