package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.enums.ComputationStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность результата вычислений по математической модели.
 */
@Entity
@Table(name = "computation_results")
@Getter
@Setter
@NoArgsConstructor
public class ComputationResult {

    /**
     * Уникальный идентификатор результата вычисления.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Математическая модель, к которой относится результат.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "math_model_id", nullable = false)
    private MathModel mathModel;

    /**
     * Дата и время начала вычисления.
     */
    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    /**
     * Дата и время завершения вычисления.
     */
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    /**
     * Текущий статус вычислений.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ComputationStatus status;

    /**
     * Сериализованные данные результата вычислений (JSON).
     */
    @Column(name = "result_data", columnDefinition = "jsonb")
    private String resultData;

    /**
     * Сообщение об ошибке (если вычисление завершилось неудачно).
     */
    @Column
    private String error;
}
