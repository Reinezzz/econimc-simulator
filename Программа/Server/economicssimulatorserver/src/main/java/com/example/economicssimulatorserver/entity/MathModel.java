package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.enums.ModelType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Сущность математической модели, принадлежащей пользователю.
 */
@Entity
@Table(name = "math_models")
@Getter
@Setter
@NoArgsConstructor
public class MathModel {

    /**
     * Уникальный идентификатор математической модели.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Владелец модели (идентификатор пользователя).
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Название модели.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Тип математической модели.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "model_type", nullable = false)
    private ModelType modelType;

    /**
     * Формула модели.
     */
    @Column
    private String formula;

    /**
     * Список параметров модели.
     */
    @OneToMany(mappedBy = "mathModel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ModelParameter> parameters = new ArrayList<>();

}
