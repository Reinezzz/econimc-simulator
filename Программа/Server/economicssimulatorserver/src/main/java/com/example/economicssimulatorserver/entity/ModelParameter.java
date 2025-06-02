package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "model_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь с моделью (многие параметры к одной модели)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EconomicModel model;

    @Column(nullable = false, length = 100)
    private String paramName;   // Краткое обозначение (например, "a", "P", "Qd")

    @Column(nullable = false, length = 50)
    private String paramType;   // Тип параметра ("double", "int", "string", "enum", "json", ...)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paramValue;  // Значение параметра в виде строки

    @Column(length = 255)
    private String displayName; // Полное название (для тултипов, если потребуется)

    @Column(columnDefinition = "TEXT")
    private String description; // Описание параметра

    @Column
    private Integer customOrder; // Для сортировки параметров в UI (опционально)
}
