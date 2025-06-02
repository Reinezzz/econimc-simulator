package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.RefreshTokenRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
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

        // Для упрощения создаём UserDetails через анонимный класс
        var userDetails = org.springframework.security.core.userdetails.User
                .withUsername("user").password("pass").authorities("USER").build();

        RefreshToken token = refreshTokenService.createRefreshToken(userDetails);
        assertThat(token).isNotNull();
        verify(refreshTokenRepo).save(any(RefreshToken.class));
    }

    @Test
    void deleteByToken_shouldDelete() {
        RefreshToken token = new RefreshToken();
        when(refreshTokenRepo.findByToken(anyString())).thenReturn(Optional.of(token));
        refreshTokenService.deleteByToken("tok");
        verify(refreshTokenRepo).delete(token);
    }

    // Аналогично для других методов
}
