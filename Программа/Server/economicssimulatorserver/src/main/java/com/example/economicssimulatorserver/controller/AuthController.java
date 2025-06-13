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
 * Контроллер обработки запросов, связанных с аутентификацией и регистрацией
 * пользователей.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CacheManager cacheManager;

    /**
     * Регистрация нового пользователя.
     *
     * @param req данные, необходимые для регистрации
     * @return {@link ApiResponse} c результатом операции
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@RequestBody @Valid RegistrationRequest req) {
        return authService.register(req);
    }

    /**
     * Подтверждение адреса электронной почты.
     *
     * @param req объект с кодом подтверждения
     * @return {@link ApiResponse} c результатом верификации
     */
    @PostMapping("/verify-email")
    public ApiResponse verifyEmail(@RequestBody @Valid VerificationRequest req) {
        return authService.verifyEmail(req);
    }

    /**
     * Авторизация пользователя.
     *
     * @param req запрос с логином и паролем
     * @return пары токенов доступа и обновления
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    /**
     * Инициализация процедуры сброса пароля.
     *
     * @param req запрос с адресом электронной почты
     * @return {@link ApiResponse} с подтверждением отправки письма
     */
    @PostMapping("/password-reset")
    public ApiResponse passwordReset(@RequestBody @Valid PasswordResetRequest req) {
        return authService.initiatePasswordReset(req);
    }

    /**
     * Подтверждение смены пароля по коду из письма.
     *
     * @param req запрос с кодом и новым паролем
     * @return {@link ApiResponse} об успешной смене пароля
     */
    @PostMapping("/password-reset/confirm")
    public ApiResponse passwordResetConfirm(@RequestBody @Valid PasswordResetConfirm req) {
        return authService.confirmPasswordReset(req);
    }

    /**
     * Отмена незавершённой регистрации пользователя.
     *
     * @param body тело запроса, содержащее адрес электронной почты
     * @return {@link ApiResponse} о результате удаления временных данных
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
     * Отмена запроса на восстановление пароля.
     *
     * @param body тело запроса, содержащее email пользователя
     * @return {@link ApiResponse} с подтверждением отмены
     */
    @PostMapping("/cancel-password-reset")
    public ApiResponse cancelPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.cancelPasswordReset(email);
        return new ApiResponse(true, "msg.password_reset_canceled");
    }

    /**
     * Получение новой пары токенов по refresh token.
     *
     * @param request запрос с действительным refresh token
     * @return {@link ResponseEntity} содержащий новые токены
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshTokens(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Завершение сеанса пользователя и отзыв refresh token.
     *
     * @param request запрос с идентификатором refresh token
     * @return ответ об успешном выходе из системы
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new ApiResponse(true, "msg.logged_out"));
    }

}
