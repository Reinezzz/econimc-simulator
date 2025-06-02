package com.example.economicssimulatorserver.util;

import java.util.*;

public class ChartDataUtil {

    /**
     * Генерирует список точек для графика по двум массивам X и Y.
     * Возвращает List<Map<String, Double>>: [{x: ..., y: ...}, ...]
     */
    public static List<Map<String, Double>> pointsList(double[] x, double[] y) {
        if (x.length != y.length)
            throw new IllegalArgumentException("Размеры массивов X и Y должны совпадать");
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
     * Формирует диапазон значений для оси (min, max, step)
     */
    public static double[] range(double min, double max, int steps) {
        if (steps <= 1)
            throw new IllegalArgumentException("Шагов должно быть больше одного");
        double[] arr = new double[steps];
        double step = (max - min) / (steps - 1);
        for (int i = 0; i < steps; i++) {
            arr[i] = min + i * step;
        }
        return arr;
    }

    /**
     * Быстрое преобразование данных для столбчатой диаграммы: Map<label, value>
     */
    public static List<Map<String, Object>> barData(String[] labels, double[] values) {
        if (labels.length != values.length)
            throw new IllegalArgumentException("Размеры labels и values должны совпадать");
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
