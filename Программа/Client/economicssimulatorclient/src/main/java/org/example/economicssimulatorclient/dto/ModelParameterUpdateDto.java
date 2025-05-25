package org.example.economicssimulatorclient.dto;

public record ModelParameterUpdateDto(
        Long id,
        String name,
        String description,
        String type,
        String value
) {}
