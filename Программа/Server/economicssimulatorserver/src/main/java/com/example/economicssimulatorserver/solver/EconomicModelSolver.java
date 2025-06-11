package com.example.economicssimulatorserver.solver;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;

public interface EconomicModelSolver {

    CalculationResponseDto solve(CalculationRequestDto request);

    String getModelType();
}
