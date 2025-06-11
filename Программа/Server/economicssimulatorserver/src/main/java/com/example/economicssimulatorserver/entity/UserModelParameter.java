package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "user_model_parameter",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "parameter_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"model", "user", "parameter"})
public class UserModelParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id")
    private EconomicModel model;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parameter_id")
    private ModelParameter parameter;

    @Column(nullable = false, length = 255)
    private String value;
}