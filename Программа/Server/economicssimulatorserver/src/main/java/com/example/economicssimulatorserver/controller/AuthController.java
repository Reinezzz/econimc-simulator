package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CacheManager cacheManager;

    /* ---------- REGISTRATION ---------- */

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse register(@RequestBody RegistrationRequest req) {
        return authService.register(req);
    }

    @PostMapping("/verify-email")
    public ApiResponse verifyEmail(@RequestBody VerificationRequest req) {
        return authService.verifyEmail(req);
    }

    /* ---------- LOGIN ---------- */

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    /* ---------- PASSWORD RESET ---------- */

    @PostMapping("/password-reset")
    public ApiResponse passwordReset(@RequestBody PasswordResetRequest req) {
        return authService.initiatePasswordReset(req);
    }

    @PostMapping("/password-reset/confirm")
    public ApiResponse passwordResetConfirm(@RequestBody PasswordResetConfirm req) {
        return authService.confirmPasswordReset(req);
    }

    @PostMapping("/cancel-registration")
    public ApiResponse cancelRegistration(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        var cache = cacheManager.getCache("registrations");
        if (cache != null && email != null) {
            cache.evict(email);
        }
        return new ApiResponse(true, "Registration cancelled");
    }

    @PostMapping("/cancel-password-reset")
    public ApiResponse cancelPasswordReset(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        authService.cancelPasswordReset(email);
        return new ApiResponse(true, "Password reset cancelled");
    }


}
