package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.util.TemplateUtil;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceTest {

    @Mock private JavaMailSender mailSender;
    @Mock private TemplateUtil templateUtil;
    @InjectMocks private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mailService = new MailService(mailSender, templateUtil);
    }

    @Test
    void sendVerificationEmail_ShouldRenderTemplateAndSend() {
        when(templateUtil.render(anyString(), anyMap())).thenReturn("<html>OK</html>");
        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        assertDoesNotThrow(() -> mailService.sendVerificationEmail("to@to.com", "username", "code"));

        verify(templateUtil).render(contains("verification_email"), anyMap());
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_ShouldRenderTemplateAndSend() {
        when(templateUtil.render(anyString(), anyMap())).thenReturn("<html>OK</html>");
        MimeMessage message = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(message);

        assertDoesNotThrow(() -> mailService.sendPasswordResetEmail("to@to.com", "username", "code"));

        verify(templateUtil).render(contains("reset_password_email"), anyMap());
        verify(mailSender).send(any(MimeMessage.class));
    }
}
