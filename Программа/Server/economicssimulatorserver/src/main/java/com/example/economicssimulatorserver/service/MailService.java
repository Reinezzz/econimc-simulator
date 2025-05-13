package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.util.TemplateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateUtil templateUtil;

    /* ---------- публичный API ---------- */

    public void sendVerificationEmail(String to, String username, String code) {
        String html = templateUtil.render(
                "verification_email.html",
                Map.of("username", username, "code", code));
        sendHtml(to, "Email verification", html);
    }

    public void sendPasswordResetEmail(String to, String username, String code) {
        String html = templateUtil.render(
                "reset_password_email.html",
                Map.of("username", username, "code", code));
        sendHtml(to, "Password reset code", html);
    }

    /* ---------- внутренний SMTP ---------- */

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);   // true = HTML
            mailSender.send(mime);

        } catch (MessagingException ex) {
            throw new IllegalStateException("Failed to send mail", ex);
        }
    }
}
