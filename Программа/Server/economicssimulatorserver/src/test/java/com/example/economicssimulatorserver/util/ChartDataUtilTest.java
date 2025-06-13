package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class ChartDataUtilTest {

    @Test
    void pointsList_shouldReturnPointsList_whenInputIsValid() {
        double[] x = {1.0, 2.0, 3.0};
        double[] y = {10.0, 20.0, 30.0};

        List<Map<String, Double>> result = ChartDataUtil.pointsList(x, y);

        assertThat(result).hasSize(3);
        assertThat(result.get(0)).containsEntry("quantity", 1.0).containsEntry("price", 10.0);
        assertThat(result.get(2)).containsEntry("quantity", 3.0).containsEntry("price", 30.0);
    }

    @Test
    void pointsList_shouldThrow_whenSizesNotMatch() {
        double[] x = {1.0, 2.0};
        double[] y = {10.0};

        assertThatThrownBy(() -> ChartDataUtil.pointsList(x, y))
                .isInstanceOf(LocalizedException.class)
                .hasMessageContaining("error.chart_xy_size");
    }

    @Test
    void range_shouldReturnArray_whenInputIsValid() {
        double[] arr = ChartDataUtil.range(0.0, 10.0, 3);
        assertThat(arr).containsExactly(0.0, 5.0, 10.0);
    }

    @Test
    void range_shouldThrow_whenStepsTooSmall() {
        assertThatThrownBy(() -> ChartDataUtil.range(0.0, 1.0, 1))
                .isInstanceOf(LocalizedException.class)
                .hasMessageContaining("error.chart_steps");
    }

    @Test
    void barData_shouldReturnBarList_whenInputIsValid() {
        String[] labels = {"A", "B"};
        double[] values = {5.0, 8.0};
        List<Map<String, Object>> bars = ChartDataUtil.barData(labels, values);

        assertThat(bars).hasSize(2);
        assertThat(bars.get(0)).containsEntry("label", "A").containsEntry("value", 5.0);
        assertThat(bars.get(1)).containsEntry("label", "B").containsEntry("value", 8.0);
    }

    @Test
    void barData_shouldThrow_whenSizesNotMatch() {
        String[] labels = {"A"};
        double[] values = {5.0, 8.0};
        assertThatThrownBy(() -> ChartDataUtil.barData(labels, values))
                .isInstanceOf(LocalizedException.class)
                .hasMessageContaining("error.chart_label_size");
    }
}
