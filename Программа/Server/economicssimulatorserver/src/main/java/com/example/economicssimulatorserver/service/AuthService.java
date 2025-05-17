package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CacheManager cacheManager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepo;

    private static final String REG_CACHE = "registrations";

    /* ---------- Регистрация через Redis Cache ---------- */
    @Transactional
    public ApiResponse register(RegistrationRequest req) {
        // Проверить уникальность e-mail и username в users и кэше
        if (userRepo.existsByEmail(req.email()))
            throw new IllegalArgumentException("Email уже занят");
        if (userRepo.existsByUsername(req.username()))
            throw new IllegalArgumentException("Имя пользователя уже занято");

        var cache = cacheManager.getCache(REG_CACHE);
        if (cache.get(req.email()) != null) {
            cache.evict(req.email());
            throw new IllegalArgumentException("Pending registration for this email already exists");
        }

        String code = String.format("%06d", (int) (Math.random() * 1_000_000));
        Instant expires = Instant.now().plusSeconds(15 * 60);

        PendingRegistration pending = new PendingRegistration(
                req.username(),
                req.email(),
                encoder.encode(req.password()),
                code,
                expires
        );

        cache.put(req.email(), pending);
        mailService.sendVerificationEmail(req.email(), req.username(), code);

        return new ApiResponse(true, "Verification code sent to email");
    }

    /* ---------- Подтверждение email ---------- */
    @Transactional
    public ApiResponse verifyEmail(VerificationRequest req) {
        var cache = cacheManager.getCache(REG_CACHE);
        PendingRegistration pending = cache.get(req.email(), PendingRegistration.class);

        if (pending == null)
            throw new IllegalArgumentException("No registration request found for this email");

        if (pending.expiresAt.isBefore(Instant.now())) {
            cache.evict(req.email());
            pending = null;
            throw new IllegalArgumentException("Verification code expired");
            //TODO: Обработка ошибок и отправка их на клиент
        }

        if (!pending.code.equals(req.code())) {
            cache.evict(req.email());
            pending = null;
            throw new IllegalArgumentException("Неверный код");
        }

        // Используем только единственный способ создания пользователя
        userService.register(
                pending.username,
                pending.email,
                pending.passwordHash
        );

        cache.evict(req.email());
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
            throw new IllegalArgumentException("Неверный логин, email или пароль");
        }
    }

    /* ---------- Сброс пароля ---------- */
    @Transactional
    public ApiResponse initiatePasswordReset(PasswordResetRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден"));

        String code = tokenService.createPasswordResetToken(user);

        mailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                code);

        return new ApiResponse(true, "Reset code sent to email");
    }

    @Transactional
    public ApiResponse confirmPasswordReset(PasswordResetConfirm req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new IllegalArgumentException("Аккаунт не найден"));
        boolean ok = tokenService.validatePasswordResetCode(user, req.code());
        if (!ok) {
            throw new IllegalArgumentException("Неверный код, или код устарел");
        }


        userService.updatePassword(user, req.newPassword());
        tokenService.evictPasswordResetToken(user);

        return new ApiResponse(true, "Password updated");
    }


    public void cancelPasswordReset(String email) {
        User user = userService.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Email не найден"));
        tokenService.evictPasswordResetToken(user); // см. ниже!
    }


}
