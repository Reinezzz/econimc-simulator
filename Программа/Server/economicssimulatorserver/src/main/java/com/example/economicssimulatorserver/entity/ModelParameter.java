package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.enums.ParameterType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * Сущность параметра математической модели.
 */
@Entity
@Table(name = "model_parameters")
@Getter
@Setter
@NoArgsConstructor
public class ModelParameter {

    /**
     * Уникальный идентификатор параметра.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Математическая модель, к которой относится параметр.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "math_model_id", nullable = false)
    private MathModel mathModel;

    /**
     * Имя параметра.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Тип параметра (double, int, string, boolean, array).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "param_type", nullable = false)
    private ParameterType paramType;

    /**
     * Значение параметра в строковом виде.
     * Для массива — значения сериализуются в JSON.
     */
    @Column(nullable = false)
    private String value;

    /**
     * Обязателен ли данный параметр для вычислений.
     */
    @Column(nullable = false)
    private boolean required;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

}
