package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST-контроллер для обработки операций, связанных с аутентификацией и регистрацией пользователей.
 * <p>
 * Включает регистрацию, подтверждение email, вход, сброс пароля и отмену заявок.
 * Все эндпоинты доступны по префиксу {@code /auth}.
 * </p>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CacheManager cacheManager;

    /* ---------- РЕГИСТРАЦИЯ ---------- */

    /**
     * Регистрация нового пользователя.
     *
     * @param req данные для регистрации
     * @return стандартный ответ с результатом регистрации
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@RequestBody @Valid RegistrationRequest req) {
        return authService.register(req);
    }

    /**
     * Подтверждение email пользователя по коду.
     *
     * @param req данные с кодом подтверждения
     * @return стандартный ответ с результатом подтверждения
     */
    @PostMapping("/verify-email")
    public ApiResponse verifyEmail(@RequestBody @Valid VerificationRequest req) {
        return authService.verifyEmail(req);
    }

    /* ---------- ВХОД ---------- */

    /**
     * Вход пользователя в систему.
     *
     * @param req логин и пароль (или email)
     * @return ответ с токеном доступа и информацией о пользователе
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    /* ---------- СБРОС ПАРОЛЯ ---------- */

    /**
     * Инициация процесса сброса пароля по email.
     *
     * @param req email пользователя
     * @return стандартный ответ о статусе отправки кода
     */
    @PostMapping("/password-reset")
    public ApiResponse passwordReset(@RequestBody @Valid PasswordResetRequest req) {
        return authService.initiatePasswordReset(req);
    }

    /**
     * Подтверждение сброса пароля по коду.
     *
     * @param req данные для подтверждения сброса (email, код, новый пароль)
     * @return стандартный ответ о статусе операции
     */
    @PostMapping("/password-reset/confirm")
    public ApiResponse passwordResetConfirm(@RequestBody @Valid PasswordResetConfirm req) {
        return authService.confirmPasswordReset(req);
    }

    /**
     * Отмена незавершённой регистрации по email.
     *
     * @param body карта с ключом "email"
     * @return ответ с результатом отмены регистрации
     */
    @PostMapping("/cancel-registration")
    public ApiResponse cancelRegistration(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        var cache = cacheManager.getCache("registrations");
        if (cache != null && email != null) {
            cache.evict(email);
        }
        return new ApiResponse(true, "msg.registration_canceled");
    }

    /**
     * Отмена сброса пароля по email.
     *
     * @param body карта с ключом "email"
     * @return ответ с результатом отмены операции
     */
    @PostMapping("/cancel-password-reset")
    public ApiResponse cancelPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.cancelPasswordReset(email);
        return new ApiResponse(true, "msg.password_reset_canceled");
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshTokens(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiResponse(true,"Successfully logged out."));
    }

}
