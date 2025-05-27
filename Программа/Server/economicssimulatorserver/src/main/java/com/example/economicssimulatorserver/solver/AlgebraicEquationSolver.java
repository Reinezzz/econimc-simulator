package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.entity.MathModel;
import com.example.economicssimulatorserver.entity.ModelParameter;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.enums.ModelType;

import org.springframework.stereotype.Component;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AlgebraicEquationSolver implements Solver {

    @Override
    public SolverResult solve(MathModel model, Map<Long, String> parameterValues) throws LocalizedException {
        // Проверка типа модели
        if (!supports(model)) {
            throw new LocalizedException("solver.unsupported_model_type");
        }
        // Получаем формулу
        String formula = model.getFormula();
        if (formula == null || formula.isBlank()) {
            throw new LocalizedException("solver.formula_is_blank");
        }

        // Собираем параметры из модели
        List<ModelParameter> params = model.getParameters();
        if (params == null || params.isEmpty()) {
            throw new LocalizedException("solver.no_parameters_in_model");
        }

        // Сопоставляем имена параметров с их значениями по id
        // (exp4j требует имена переменных, не id)
        Map<String, Double> variables = params.stream()
                .collect(Collectors.toMap(
                        ModelParameter::getName, // переменная по имени
                        p -> {
                            String valueStr = parameterValues.get(p.getId());
                            if (valueStr == null) {
                                throw new RuntimeException("solver.parameter_not_found:" + p.getName());
                            }
                            try {
                                return Double.parseDouble(valueStr);
                            } catch (NumberFormatException ex) {
                                throw new RuntimeException("solver.invalid_parameter:" + p.getName());
                            }
                        }
                ));

        // Строим выражение
        Expression expression;
        try {
            expression = new ExpressionBuilder(formula)
                    .variables(variables.keySet())
                    .build()
                    .setVariables(variables);
        } catch (Exception ex) {
            throw new LocalizedException("solver.invalid_formula_syntax", ex);
        }

        // Вычисляем результат
        double result;
        try {
            result = expression.evaluate();
            if (Double.isNaN(result) || Double.isInfinite(result)) {
                throw new LocalizedException("solver.result_invalid");
            }
        } catch (Exception ex) {
            throw new LocalizedException("solver.evaluation_error", ex);
        }

        // Возвращаем результат через SolverResult (у тебя уже реализован класс)
        return new SolverResult(String.valueOf(result));
    }

    @Override
    public boolean supports(MathModel model) {
        // Поддерживаем только модели типа "алгебраическое уравнение"
        return model.getModelType() == ModelType.ALGEBRAIC_EQUATION;
    }
}
