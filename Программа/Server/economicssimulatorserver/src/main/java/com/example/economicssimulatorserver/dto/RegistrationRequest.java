package com.example.economicssimulatorserver.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegistrationRequest(
        @NotBlank String username,
        @Email String email,
        @NotBlank String password
) {}
