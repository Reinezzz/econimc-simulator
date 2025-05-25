package org.example.economicssimulatorclient.dto;

public record ModelParameterCreateDto(
        String name,
        String description,
        String type
) {}
