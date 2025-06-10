package com.example.economicssimulatorserver.dto;

import java.time.LocalDateTime;

public record DocumentDto(
        Long id,
        Long userId,
        String name,
        String path,
        LocalDateTime uploadedAt
) {}
