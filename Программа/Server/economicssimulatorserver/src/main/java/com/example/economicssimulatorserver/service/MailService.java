package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.util.TemplateUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateUtil templateUtil;
    private final MessageSource messageSource;
    private String defaultLanguage;

    public void sendVerificationEmail(String to, String username, String code) {
        sendVerificationEmail(to, username, code, defaultLanguage);
    }

    public void sendVerificationEmail(String to, String username, String code, String language) {
        String template = "verification_email_" + language + ".html";
        String html = templateUtil.render(
                "verification_email.html",
                Map.of("username", username, "code", code));
        Locale locale = Locale.forLanguageTag(language);
        String subject = messageSource.getMessage("msg.email_verification_subject", null, locale);
        sendHtml(to, subject, html);
    }

    public void sendPasswordResetEmail(String to, String username, String code) {
        sendPasswordResetEmail(to, username, code, defaultLanguage);
    }

    public void sendPasswordResetEmail(String to, String username, String code, String language) {
        String template = "reset_password_email_" + language + ".html";
        String html = templateUtil.render(
                "reset_password_email.html",
                Map.of("username", username, "code", code));
        Locale locale = Locale.forLanguageTag(language);
        String subject = messageSource.getMessage("msg.password_reset_subject", null, locale);
        sendHtml(to, subject, html);
    }

    void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(mime);

        } catch (MessagingException ex) {
            throw new LocalizedException("error.mail_send", ex.getMessage());
        }
    }
}
