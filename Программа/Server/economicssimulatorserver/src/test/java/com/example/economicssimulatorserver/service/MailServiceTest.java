package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.util.TemplateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateUtil templateUtil;

    @InjectMocks
    private MailService mailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    void sendVerificationEmail_shouldNotThrow() {
        when(templateUtil.render(anyString(), anyMap())).thenReturn("<html>test</html>");

        assertThatCode(() ->
                mailService.sendVerificationEmail("to@mail.com", "user", "code123")
        ).doesNotThrowAnyException();

        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    void sendPasswordResetEmail_shouldNotThrow() {
        when(templateUtil.render(anyString(), anyMap())).thenReturn("<html>reset</html>");

        assertThatCode(() ->
                mailService.sendPasswordResetEmail("reset@mail.com", "resetuser", "resetcode")
        ).doesNotThrowAnyException();

        verify(mailSender, atLeastOnce()).send(any(MimeMessage.class));
    }

}
