package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Сущность, связывающая пользователя и параметры модели.
 */
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

    /**
     * Уникальный идентификатор связи.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Пользователь, которому принадлежит значение параметра.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Экономическая модель, к которой относится параметр.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id")
    private EconomicModel model;

    /**
     * Параметр модели.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "parameter_id")
    private ModelParameter parameter;

    /**
     * Значение параметра, заданное пользователем.
     */
    @Column(nullable = false, length = 255)
    private String value;
}
