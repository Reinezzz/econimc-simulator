package org.example.economicssimulatorclient.dto;

/**
 * Изображение графика, включаемое в отчёт.
 *
 * @param chartType  тип графика
 * @param imageBase64 изображение в формате Base64
 */
public record ReportChartImageDto(
        String chartType,
        String imageBase64
) {}
