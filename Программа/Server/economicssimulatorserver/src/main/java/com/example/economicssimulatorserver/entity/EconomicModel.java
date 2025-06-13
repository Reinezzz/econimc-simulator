package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Сущность экономической модели с параметрами и результатами расчётов.
 */
@Entity
@Table(name = "economic_models")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EconomicModel {

    /**
     * Уникальный идентификатор модели.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Тип модели, например, «линейная» или «нелинейная».
     */
    @Column(nullable = false, length = 100)
    private String modelType;

    /**
     * Отображаемое название модели.
     */
    @Column(nullable = false, length = 255)
    private String name;

    /**
     * Описание модели.
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * Параметры, используемые в модели.
     */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ModelParameter> parameters;

    /**
     * Результаты, полученные после расчёта модели.
     */
    @OneToMany(mappedBy = "model", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ModelResult> results;

    /**
     * Дата и время создания модели.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * Дата и время последнего обновления модели.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    /**
     * Формула модели в виде строки.
     */
    @Column(updatable = false)
    private String formula;

    /**
     * Обновляет время изменения модели перед сохранением.
     */
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
