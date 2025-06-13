package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.util.JwtUtil;
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

import java.time.Instant;
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
    @Mock private Cache cache;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
    }

    @Test
    void register_whenUserExists_shouldThrowException() {
        when(userRepo.existsByEmail(anyString())).thenReturn(true);
        RegistrationRequest req = new RegistrationRequest("user", "mail@mail.com", "pass");
        assertThatThrownBy(() -> authService.register(req)).isInstanceOf(LocalizedException.class);
    }

    @Test
    void register_newUser_shouldSendVerification() {
        RegistrationRequest req = new RegistrationRequest("user", "mail@mail.com", "pass");
        when(userRepo.existsByEmail(anyString())).thenReturn(false);
        when(userRepo.existsByUsername(anyString())).thenReturn(false);
        when(cache.get(anyString())).thenReturn(null);
        when(encoder.encode(anyString())).thenReturn("hashed");
        doNothing().when(mailService).sendVerificationEmail(anyString(), anyString(), anyString());

        ApiResponse response = authService.register(req);
        assertThat(response.success()).isTrue();
        verify(mailService).sendVerificationEmail(eq("mail@mail.com"), eq("user"), anyString());
    }

    @Test
    void login_whenValid_shouldReturnTokens() {
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("pass");
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));

        Authentication auth = mock(Authentication.class);
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user").password("pass").authorities("USER").build();
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtil.generateToken(any(UserDetails.class))).thenReturn("jwt");

        RefreshToken mockToken = mock(RefreshToken.class);
        when(mockToken.getToken()).thenReturn("refresh");
        when(refreshTokenService.createRefreshToken(any(UserDetails.class))).thenReturn(mockToken);

        LoginRequest req = new LoginRequest("user", "pass");
        LoginResponse resp = authService.login(req);

        assertThat(resp.accessToken()).isEqualTo("jwt");
        assertThat(resp.refreshToken()).isEqualTo("refresh");
    }

}
