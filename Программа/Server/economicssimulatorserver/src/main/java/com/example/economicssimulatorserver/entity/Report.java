package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

/**
 * Сущность отчёта, сформированного по результатам работы модели.
 */
@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    /**
     * Уникальный идентификатор отчёта.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя, сформировавшего отчёт.
     */
    private Long userId;

    /**
     * Идентификатор модели, по которой создан отчёт.
     */
    private Long modelId;

    /**
     * Название отчёта.
     */
    private String name;

    /**
     * Название модели.
     */
    private String modelName;

    /**
     * Путь к файлу отчёта на сервере.
     */
    private String path;

    /**
     * Дата и время создания отчёта.
     */
    private OffsetDateTime createdAt;

    /**
     * Язык отчёта.
     */
    private String language;

    /**
     * Параметры, использованные при расчёте модели.
     */
    @Column(name = "params", columnDefinition = "text")
    private String params;

    /**
     * Результат расчёта модели.
     */
    @Column(name = "result", columnDefinition = "text")
    private String result;

    /**
     * Сообщения от LLM, использованные при формировании отчёта.
     */
    @Column(name = "llm_messages", columnDefinition = "text")
    private String llmMessages;
}