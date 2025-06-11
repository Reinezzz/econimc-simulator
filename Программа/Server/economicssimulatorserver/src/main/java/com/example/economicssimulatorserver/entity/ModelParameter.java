package com.example.economicssimulatorserver.entity;

import lombok.*;
import jakarta.persistence.*;

@Entity
@Table(name = "model_parameters")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"model"})
@Builder
public class ModelParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private EconomicModel model;

    @Column(nullable = false, length = 100)
    private String paramName;

    @Column(nullable = false, length = 50)
    private String paramType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String paramValue;

    @Column(length = 255)
    private String displayName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Integer customOrder;
}
