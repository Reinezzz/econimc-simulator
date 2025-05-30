package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepo, refreshTokenService, passwordEncoder);
    }

    @Test
    void register_shouldSaveNewUser() {
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        userService.register("testuser", "email@mail.com", "passwordHash");
        verify(userRepo).save(any(User.class));
    }

    @Test
    void findByEmail_shouldReturnUser() {
        User user = new User();
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        assertThat(userService.findByEmail("test@mail.com")).contains(user);
    }

    // Добавь тесты на updatePassword, findByUsername, existsByEmail, etc.

}
