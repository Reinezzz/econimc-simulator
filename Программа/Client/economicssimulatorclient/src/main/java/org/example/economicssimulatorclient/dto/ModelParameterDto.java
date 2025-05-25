package org.example.economicssimulatorclient.dto;

public record ModelParameterDto(
        Long id,
        String name,
        String description,
        String type,
        String value
) {}
