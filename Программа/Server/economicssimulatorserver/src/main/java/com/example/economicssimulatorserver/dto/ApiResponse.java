package com.example.economicssimulatorserver.dto;

/** Унифицированный ответ‑обёртка. */
public record ApiResponse(
        boolean success,
        String message
) {}
