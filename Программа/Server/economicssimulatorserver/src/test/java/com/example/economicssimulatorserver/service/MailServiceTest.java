package com.example.economicssimulatorserver.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class MailServiceTest {

    @InjectMocks private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Настройка для mailSender/mock SMTP если нужно
    }

    @Test
    void sendVerificationEmail_shouldNotThrow() {
        // Здесь просто проверяешь что метод работает без ошибок
        mailService.sendVerificationEmail("mail@test.com", "user", "code");
        // Можно добавить verify к mailSender/send (если мокается)
    }

    // Аналогично для sendPasswordResetEmail
}
