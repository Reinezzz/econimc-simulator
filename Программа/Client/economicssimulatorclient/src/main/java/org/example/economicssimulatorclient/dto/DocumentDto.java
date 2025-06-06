package org.example.economicssimulatorclient.dto;

import java.time.LocalDateTime;

public record DocumentDto(
        Long id,
        Long userId,
        String name,
        String path,
        LocalDateTime uploadedAt,
        String description
) {}
