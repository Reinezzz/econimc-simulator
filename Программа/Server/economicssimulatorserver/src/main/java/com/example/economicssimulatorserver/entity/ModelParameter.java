package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;

/**
 * Параметр экономической модели.
 */
@Entity
@Table(name = "model_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"model"})
@Builder
public class ModelParameter {

    /**
     * Уникальный идентификатор параметра.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Экономическая модель, к которой относится параметр.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EconomicModel model;

    /**
     * Имя параметра.
     */
    @Column(nullable = false, length = 100)
    private String paramName;

    /**
     * Тип параметра.
     */
    @Column(nullable = false, length = 50)
    private String paramType;

    /**
     * Значение параметра в текстовом виде.
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String paramValue;

    /**
     * Отображаемое имя параметра.
     */
    @Column(length = 255)
    private String displayName;

    /**
     * Описание параметра.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Порядок отображения параметра.
     */
    @Column
    private Integer customOrder;
}