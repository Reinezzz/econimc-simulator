package com.example.economicssimulatorserver.dto;

/**
 * Изображение графика, включаемого в отчёт.
 *
 * @param chartType   тип графика (bar, pie и т.д.)
 * @param imageBase64 изображение в формате Base64
 */
public record ReportChartImageDto(
        String chartType,
        String imageBase64
) {}
