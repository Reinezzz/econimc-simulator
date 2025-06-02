package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.PasswordResetTokenRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.repository.VerificationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;

class TokenServiceTest {

    @Mock private PasswordResetTokenRepository passwordResetTokenRepo;
    @Mock private UserRepository userRepository;
    @Mock private VerificationTokenRepository verificationTokenRepository;
    @InjectMocks private TokenService tokenService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tokenService = new TokenService(verificationTokenRepository,passwordResetTokenRepo);
    }

    @Test
    void createPasswordResetToken_shouldReturnToken() {
        User user = new User();
        String code = tokenService.createPasswordResetToken(user);
        assertThat(code).isNotNull();
    }

    // Аналогично для других методов: validatePasswordResetCode, evictPasswordResetToken, etc.

}
