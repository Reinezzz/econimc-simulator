package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;

public interface EconomicModelSolver {
    /**
     * @param request данные и параметры для расчетов
     * @return CalculationResponseDto с результатами, готовыми для возврата клиенту
     */
    CalculationResponseDto solve(CalculationRequestDto request);

    /**
     * @return строковый тип модели, с которым работает этот solver, например "DemandSupply"
     */
    String getModelType();
}
