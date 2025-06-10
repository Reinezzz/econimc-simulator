package org.example.economicssimulatorclient.dto;

public record ReportChartImageDto(
        String chartType,    // Тип/название графика
        String imageBase64   // Base64 PNG изображения графика
) {}
