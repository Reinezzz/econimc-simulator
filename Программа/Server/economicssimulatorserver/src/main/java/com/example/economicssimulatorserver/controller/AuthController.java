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

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CacheManager cacheManager;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@RequestBody @Valid RegistrationRequest req) {
        return authService.register(req);
    }

    @PostMapping("/verify-email")
    public ApiResponse verifyEmail(@RequestBody @Valid VerificationRequest req) {
        return authService.verifyEmail(req);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/password-reset")
    public ApiResponse passwordReset(@RequestBody @Valid PasswordResetRequest req) {
        return authService.initiatePasswordReset(req);
    }

    @PostMapping("/password-reset/confirm")
    public ApiResponse passwordResetConfirm(@RequestBody @Valid PasswordResetConfirm req) {
        return authService.confirmPasswordReset(req);
    }

    @PostMapping("/cancel-registration")
    public ApiResponse cancelRegistration(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        var cache = cacheManager.getCache("registrations");
        if (cache != null && email != null) {
            cache.evict(email);
        }
        return new ApiResponse(true, "msg.registration_canceled");
    }


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
        return ResponseEntity.ok(new ApiResponse(true, "msg.logged_out"));
    }

}
