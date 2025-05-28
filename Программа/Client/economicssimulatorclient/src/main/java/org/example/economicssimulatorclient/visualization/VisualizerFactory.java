package org.example.economicssimulatorclient.visualization;

import org.example.economicssimulatorclient.dto.MathModelDto;
import org.example.economicssimulatorclient.enums.ModelType;

public class VisualizerFactory {

    public static Visualizer getVisualizer(MathModelDto model) {
        if (model == null || model.type() == null)
            throw new IllegalArgumentException("Модель или её тип не заданы");

        ModelType type = ModelType.valueOf(model.type());

        return switch (type) {
            case ALGEBRAIC_EQUATION -> new AlgebraicEquationVisualizer();
            case SYSTEM_OF_EQUATIONS -> new SystemOfEquationsVisualizer();
            case REGRESSION -> new RegressionVisualizer();
            case OPTIMIZATION -> new OptimizationVisualizer();
            default -> throw new UnsupportedOperationException("Нет визуализатора для типа модели: " + type);
        };
    }
}
