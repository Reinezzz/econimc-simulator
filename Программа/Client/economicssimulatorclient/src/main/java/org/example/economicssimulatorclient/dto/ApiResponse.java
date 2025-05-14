package org.example.economicssimulatorclient.dto;

/** Унифицированный ответ от сервера. */
public record ApiResponse(
        boolean success,
        String message
) {}
