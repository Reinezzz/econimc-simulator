package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    /* ---------- Регистрация ---------- */

    @Transactional
    public ApiResponse register(RegistrationRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String code = tokenService.createVerificationToken(user);

        /* было: mailService.sendPasswordResetEmail(user.getEmail(), code); */
        mailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),   // <‑‑‑ новый аргумент
                code);

        return new ApiResponse(true, "Reset code sent to email");
    }

    /* ---------- Подтверждение e‑mail ---------- */

    @Transactional
    public ApiResponse verifyEmail(VerificationRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean ok = tokenService.validateVerificationCode(user, req.code());
        if (!ok) throw new IllegalArgumentException("Invalid or expired code");

        userService.enableUser(user);
        return new ApiResponse(true, "Email confirmed. You can now log in.");
    }

    /* ---------- Авторизация ---------- */

    public LoginResponse login(LoginRequest req) {
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    req.usernameOrEmail(), req.password());
            var auth = authManager.authenticate(authToken);
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            String access = jwtUtil.generateToken(userDetails);
            return new LoginResponse(access, "Bearer");
        } catch (AuthenticationException ex) {
            throw new IllegalArgumentException("Bad credentials");
        }
    }

    /* ---------- Сброс пароля ---------- */

    @Transactional
    public ApiResponse initiatePasswordReset(PasswordResetRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));

        String code = tokenService.createPasswordResetToken(user);

        /* было: mailService.sendPasswordResetEmail(user.getEmail(), code); */
        mailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),   // <‑‑‑ новый аргумент
                code);

        return new ApiResponse(true, "Reset code sent to email");
    }


    @Transactional
    public ApiResponse confirmPasswordReset(PasswordResetConfirm req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Email not found"));
        boolean ok = tokenService.validatePasswordResetCode(user, req.code());
        if (!ok) throw new IllegalArgumentException("Invalid or expired code");
        // заменить пароль
        user.setPasswordHash(
                userService.loadUserByUsername(user.getUsername())  // получить encoder
                        .getPassword()); // Здесь не нужен, упростим:
        user.setPasswordHash(
                ((org.springframework.security.crypto.password.PasswordEncoder)
                        org.springframework.security.crypto.factory.PasswordEncoderFactories
                                .createDelegatingPasswordEncoder())
                        .encode(req.newPassword()));

        return new ApiResponse(true, "Password updated");
    }
}
