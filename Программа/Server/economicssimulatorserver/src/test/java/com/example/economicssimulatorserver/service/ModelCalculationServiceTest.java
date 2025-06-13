package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.CalculationRequestDto;
import com.example.economicssimulatorserver.dto.CalculationResponseDto;
import com.example.economicssimulatorserver.entity.EconomicModel;
import com.example.economicssimulatorserver.entity.ModelResult;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.exception.LocalizedException;
import com.example.economicssimulatorserver.repository.EconomicModelRepository;
import com.example.economicssimulatorserver.repository.ModelResultRepository;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.solver.EconomicModelSolver;
import com.example.economicssimulatorserver.solver.SolverFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelCalculationServiceTest {

    @Mock private EconomicModelRepository economicModelRepository;
    @Mock private ModelResultRepository modelResultRepository;
    @Mock private SolverFactory solverFactory;
    @Mock private UserRepository userRepository;
    @Mock private EconomicModelSolver modelSolver;
    @InjectMocks private ModelCalculationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void calculate_whenModelNotFound_shouldThrow() {
        when(economicModelRepository.findById(anyLong())).thenReturn(Optional.empty());
        CalculationRequestDto req = new CalculationRequestDto(1L, "type", null);
        assertThatThrownBy(() -> service.calculate(req, 123L)).isInstanceOf(LocalizedException.class);
    }

    @Test
    void calculate_whenSolverNotFound_shouldThrow() {
        when(economicModelRepository.findById(anyLong())).thenReturn(Optional.of(new EconomicModel()));
        when(solverFactory.getSolver(anyString())).thenReturn(null);
        CalculationRequestDto req = new CalculationRequestDto(1L, "type", null);
        assertThatThrownBy(() -> service.calculate(req, 123L)).isInstanceOf(LocalizedException.class);
    }

    @Test
    void calculate_shouldReturnResultAndSave() {
        EconomicModel model = new EconomicModel();
        model.setId(1L);
        User user = new User();
        user.setId(123L);
        CalculationRequestDto req = new CalculationRequestDto(1L, "type", null);
        CalculationResponseDto resp = new CalculationResponseDto(null, null);

        when(economicModelRepository.findById(anyLong())).thenReturn(Optional.of(model));
        when(solverFactory.getSolver(anyString())).thenReturn(modelSolver);
        when(modelSolver.solve(any())).thenReturn(resp);
        when(userRepository.getReferenceById(anyLong())).thenReturn(user);

        CalculationResponseDto result = service.calculate(req, 123L);
        assertThat(result).isNotNull();
        verify(modelSolver).solve(any());
    }
}
