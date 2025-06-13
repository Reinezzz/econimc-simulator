package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Test
    void updatePassword_shouldEncodeAndSave() {
        User user = new User();
        when(passwordEncoder.encode(anyString())).thenReturn("hashed");
        userService.updatePassword(user, "pass");
        assertThat(user.getPasswordHash()).isEqualTo("hashed");
        verify(userRepo).save(user);
    }

    @Test
    void loadUserByUsername_shouldThrowIfNotFound() {
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.loadUserByUsername("notfound"))
                .isInstanceOf(LocalizedException.class);
    }
}
