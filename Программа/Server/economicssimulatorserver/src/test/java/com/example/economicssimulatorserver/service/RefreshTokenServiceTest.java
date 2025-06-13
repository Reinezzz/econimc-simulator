package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.RefreshTokenRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class RefreshTokenServiceTest {

    @Mock private RefreshTokenRepository refreshTokenRepo;
    @Mock private UserRepository userRepo;
    @InjectMocks private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        refreshTokenService = new RefreshTokenService(refreshTokenRepo, userRepo);
    }

    @Test
    void createRefreshToken_shouldSaveToken() {
        User user = new User();
        when(userRepo.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(user));
        when(refreshTokenRepo.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user").password("pass").authorities("USER").build();
        RefreshToken token = refreshTokenService.createRefreshToken(userDetails);
        assertThat(token).isNotNull();
        verify(refreshTokenRepo).save(any(RefreshToken.class));
    }

    @Test
    void validateRefreshToken_shouldThrowIfNotFound() {
        when(refreshTokenRepo.findByToken(anyString())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("nope"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void validateRefreshToken_shouldThrowIfExpired() {
        RefreshToken token = new RefreshToken();
        token.setExpiryDate(Instant.now().minusSeconds(10));
        when(refreshTokenRepo.findByToken(anyString())).thenReturn(Optional.of(token));
        assertThatThrownBy(() -> refreshTokenService.validateRefreshToken("expired"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void deleteByToken_shouldDelete() {
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepo.findByToken(anyString())).thenReturn(Optional.of(token));
        refreshTokenService.deleteByToken("tok");
        verify(refreshTokenRepo).delete(token);
    }
}
