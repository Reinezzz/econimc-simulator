package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.util.JwtUtil;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock private UserService userService;
    @Mock private TokenService tokenService;
    @Mock private MailService mailService;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;
    @Mock private CacheManager cacheManager;
    @Mock private PasswordEncoder encoder;
    @Mock private UserRepository userRepo;
    @Mock private Cache cache;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        authService = new AuthService(userService, tokenService, mailService, authManager, jwtUtil, cacheManager, encoder, userRepo);
    }

    @Test
    void register_ShouldThrowIfEmailTaken() {
        RegistrationRequest req = new RegistrationRequest("u", "e@e.com", "p");
        when(userRepo.existsByEmail("e@e.com")).thenReturn(true);

        assertThrows(LocalizedException.class, () -> authService.register(req));
    }

    @Test
    void register_ShouldThrowIfUsernameTaken() {
        RegistrationRequest req = new RegistrationRequest("u", "e@e.com", "p");
        when(userRepo.existsByEmail("e@e.com")).thenReturn(false);
        when(userRepo.existsByUsername("u")).thenReturn(true);

        assertThrows(LocalizedException.class, () -> authService.register(req));
    }

    @Test
    void register_ShouldSendMailAndCacheRegistration() {
        RegistrationRequest req = new RegistrationRequest("u", "e@e.com", "p");
        when(userRepo.existsByEmail("e@e.com")).thenReturn(false);
        when(userRepo.existsByUsername("u")).thenReturn(false);
        when(cache.get(eq("e@e.com"))).thenReturn(null);

        ApiResponse resp = authService.register(req);

        assertTrue(resp.success());
        verify(mailService, times(1)).sendVerificationEmail(eq("e@e.com"), eq("u"), anyString());
        verify(cache, times(1)).put(eq("e@e.com"), any());
    }

    @Test
    void register_ShouldThrowIfAlreadyInCache() {
        RegistrationRequest req = new RegistrationRequest("u", "e@e.com", "p");
        when(userRepo.existsByEmail("e@e.com")).thenReturn(false);
        when(userRepo.existsByUsername("u")).thenReturn(false);

        assertThrows(LocalizedException.class, () -> authService.register(req));
    }

    @Test
    void verifyEmail_ShouldThrowIfNoRegistration() {
        VerificationRequest req = new VerificationRequest("e@e.com", "123");
        when(cache.get(eq("e@e.com"), eq(PendingRegistration.class))).thenReturn(null);

        assertThrows(LocalizedException.class, () -> authService.verifyEmail(req));
    }

    @Test
    void login_ShouldReturnToken_WhenCredentialsValid() {
        LoginRequest req = new LoginRequest("u", "p");
        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("u").password("p").authorities().build();

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn("token");

        LoginResponse resp = authService.login(req);

        assertEquals("token", resp.accessToken());
    }

    @Test
    void login_ShouldThrowException_WhenCredentialsInvalid() {
        LoginRequest req = new LoginRequest("u", "badpass");
        when(authManager.authenticate(any())).thenThrow(new RuntimeException());

        assertThrows(LocalizedException.class, () -> authService.login(req));
    }

    @Test
    void initiatePasswordReset_ShouldSendEmail_WhenUserFound() {
        PasswordResetRequest req = new PasswordResetRequest("e@e.com");
        User user = new User();
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.of(user));
        when(tokenService.createPasswordResetToken(user)).thenReturn("123456");

        ApiResponse resp = authService.initiatePasswordReset(req);

        assertTrue(resp.success());
        verify(mailService).sendPasswordResetEmail(anyString(), anyString(), anyString());
    }

    @Test
    void initiatePasswordReset_ShouldThrow_WhenUserNotFound() {
        PasswordResetRequest req = new PasswordResetRequest("e@e.com");
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.empty());

        assertThrows(LocalizedException.class, () -> authService.initiatePasswordReset(req));
    }

    @Test
    void confirmPasswordReset_ShouldSucceed_WhenCodeValid() {
        PasswordResetConfirm req = new PasswordResetConfirm("e@e.com", "123", "pw");
        User user = new User();
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.of(user));
        when(tokenService.validatePasswordResetCode(user, "123")).thenReturn(true);

        ApiResponse resp = authService.confirmPasswordReset(req);

        assertTrue(resp.success());
        verify(userService).updatePassword(user, "pw");
        verify(tokenService).evictPasswordResetToken(user);
    }

    @Test
    void confirmPasswordReset_ShouldThrow_WhenCodeInvalid() {
        PasswordResetConfirm req = new PasswordResetConfirm("e@e.com", "bad", "pw");
        User user = new User();
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.of(user));
        when(tokenService.validatePasswordResetCode(user, "bad")).thenReturn(false);

        assertThrows(LocalizedException.class, () -> authService.confirmPasswordReset(req));
    }

    @Test
    void cancelPasswordReset_ShouldEvictToken_WhenUserFound() {
        User user = new User();
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> authService.cancelPasswordReset("e@e.com"));
        verify(tokenService).evictPasswordResetToken(user);
    }

    @Test
    void cancelPasswordReset_ShouldThrow_WhenUserNotFound() {
        when(userService.findByEmail("e@e.com")).thenReturn(Optional.empty());

        assertThrows(LocalizedException.class, () -> authService.cancelPasswordReset("e@e.com"));
    }
}
