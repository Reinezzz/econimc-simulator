package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.exception.LocalizedException;

import java.util.*;

/**
 * Набор вспомогательных методов для подготовки данных графиков.
 */
public class ChartDataUtil {

    /**
     * Преобразует массивы значений в список точек для отображения на графике.
     *
     * @param x массив значений по оси X (количество)
     * @param y массив значений по оси Y (цена)
     * @return список точек с ключами {@code "quantity"} и {@code "price"}
     * @throws LocalizedException если размеры массивов не совпадают
     */
    public static List<Map<String, Double>> pointsList(double[] x, double[] y) {
        if (x.length != y.length)
            throw new LocalizedException("error.chart_xy_size");
        List<Map<String, Double>> points = new ArrayList<>(x.length);
        for (int i = 0; i < x.length; i++) {
            Map<String, Double> point = new HashMap<>();
            point.put("quantity", x[i]);
            point.put("price", y[i]);
            points.add(point);
        }
        return points;
    }

    /**
     * Формирует массив значений в указанном диапазоне.
     *
     * @param min   минимальное значение диапазона
     * @param max   максимальное значение диапазона
     * @param steps количество шагов (точек)
     * @return массив значений
     * @throws LocalizedException если {@code steps} меньше двух
     */
    public static double[] range(double min, double max, int steps) {
        if (steps <= 1)
            throw new LocalizedException("error.chart_steps");
        double[] arr = new double[steps];
        double step = (max - min) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            arr[i] = min + i * step;
        }
        return arr;
    }

    /**
     * Создаёт список данных для столбиковой диаграммы.
     *
     * @param labels массив подписей столбцов
     * @param values массив значений столбцов
     * @return список объектов с ключами {@code "label"} и {@code "value"}
     * @throws LocalizedException если размеры массивов не совпадают
     */
    public static List<Map<String, Object>> barData(String[] labels, double[] values) {
        if (labels.length != values.length)
            throw new LocalizedException("error.chart_label_size");
        List<Map<String, Object>> bars = new ArrayList<>(labels.length);
        for (int i = 0; i < labels.length; i++) {
            Map<String, Object> bar = new HashMap<>();
            bar.put("label", labels[i]);
            bar.put("value", values[i]);
            bars.add(bar);
        }
        return bars;
    }
}
