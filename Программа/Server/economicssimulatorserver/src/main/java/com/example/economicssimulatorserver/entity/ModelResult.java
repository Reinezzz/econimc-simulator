package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "model_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModelResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // связь с моделью (одна модель — много результатов)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EconomicModel model;

    @Column(nullable = false, length = 100)
    private String resultType; // Например: "chart", "calculation", "table"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String resultData; // Результат в виде JSON-строки или plain text

    @Column(nullable = false)
    private LocalDateTime calculatedAt = LocalDateTime.now();
}
