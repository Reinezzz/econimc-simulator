package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Результат расчёта экономической модели.
 */
@Entity
@Table(name = "model_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelResult {

    /**
     * Уникальный идентификатор результата.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Модель, для которой получен результат.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EconomicModel model;

    /**
     * Тип результата.
     */
    @Column(nullable = false, length = 100)
    private String resultType;

    /**
     * Данные результата в текстовом виде.
     */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultData;

    /**
     * Время, когда был произведён расчёт.
     */
    @Column(nullable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();

    /**
     * Пользователь, выполнивший расчёт.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}