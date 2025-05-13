package com.example.economicssimulatorserver.controller;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
}
