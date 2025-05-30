package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private TokenService tokenService;
    @Mock private MailService mailService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private CacheManager cacheManager;
    @Mock private PasswordEncoder encoder;
    @Mock private UserRepository userRepo;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authService = new AuthService(userService, tokenService, mailService, refreshTokenService, authManager, jwtUtil, cacheManager, encoder, userRepo);
    }

    @Test
    void register_whenUserExists_shouldThrowException() {
        when(userRepo.existsByEmail(anyString())).thenReturn(true);
        RegistrationRequest req = new RegistrationRequest("user", "mail@mail.com", "pass");
        assertThatThrownBy(() -> authService.register(req)).isInstanceOf(RuntimeException.class);
    }

    @Test
    void login_whenValid_shouldReturnTokens() {
        // Мокаем всё, включая userRepo, authManager, jwtUtil, refreshTokenService
        User user = new User();
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        // Допиши сюда mock для authManager, jwtUtil, refreshTokenService, если нужны детали
    }

    // Дополняй аналогично для других public-методов: verifyEmail, initiatePasswordReset, confirmPasswordReset, etc.

}
