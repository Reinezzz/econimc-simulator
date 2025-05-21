package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.PasswordResetTokenRepository;
import com.example.economicssimulatorserver.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {

    @Mock private VerificationTokenRepository verifyRepo;
    @Mock private PasswordResetTokenRepository resetRepo;
    @InjectMocks private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(verifyRepo, resetRepo);
    }

    @Test
    void createPasswordResetToken_ShouldCreateAndSaveToken() {
        User user = new User();
        doNothing().when(resetRepo).deleteByUser(user);
        when(resetRepo.save(any(PasswordResetToken.class))).thenAnswer(i -> i.getArguments()[0]);

        String code = tokenService.createPasswordResetToken(user);

        assertNotNull(code);
        verify(resetRepo).deleteByUser(user);
        verify(resetRepo).save(any(PasswordResetToken.class));
    }

    @Test
    void validatePasswordResetCode_ShouldReturnTrueAndDelete_WhenCorrectAndNotExpired() {
        User user = new User();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .code("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        when(resetRepo.findByUser(user)).thenReturn(Optional.of(token));

        boolean ok = tokenService.validatePasswordResetCode(user, "123456");

        assertTrue(ok);
        verify(resetRepo).delete(token);
    }

    @Test
    void validatePasswordResetCode_ShouldReturnFalse_WhenCodeIncorrectOrExpired() {
        User user = new User();
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .code("654321")
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();
        when(resetRepo.findByUser(user)).thenReturn(Optional.of(token));

        boolean ok = tokenService.validatePasswordResetCode(user, "123456");
        assertFalse(ok);

        // expired
        ok = tokenService.validatePasswordResetCode(user, "654321");
        assertFalse(ok);
    }

    @Test
    void evictPasswordResetToken_ShouldDeleteToken() {
        User user = new User();
        doNothing().when(resetRepo).deleteByUser(user);

        assertDoesNotThrow(() -> tokenService.evictPasswordResetToken(user));
        verify(resetRepo).deleteByUser(user);
    }
}
