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

/**
 * Сервис для отправки email-писем с кодами подтверждения и сброса пароля.
 */
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final TemplateUtil templateUtil;
    private final MessageSource messageSource;
    private String defaultLanguage;

    /**
     * Отправляет письмо с кодом подтверждения email.
     * @param to email получателя
     * @param username имя пользователя
     * @param code одноразовый код подтверждения
     */
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

    /**
     * Отправляет письмо с кодом для сброса пароля.
     * @param to email получателя
     * @param username имя пользователя
     * @param code одноразовый код сброса пароля
     */
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

    /**
     * Отправляет HTML-письмо через SMTP.
     * @param to email получателя
     * @param subject тема письма
     * @param html содержимое письма в формате HTML
     * @throws IllegalStateException если не удалось отправить письмо
     */
    void sendHtml(String to, String subject, String html) {
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
            throw new LocalizedException("error.mail_send", ex.getMessage());
        }
    }
}
