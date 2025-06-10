package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private Long modelId;

    private String name;

    private String modelName;

    private String path;

    private OffsetDateTime createdAt;

    private String language;

    // Для простоты — параметры, результат и llm-сообщения храним как JSON
    @Column(name = "params", columnDefinition = "text")
    private String params;

    @Column(name = "result", columnDefinition = "text")
    private String result;

    @Column(name = "llm_messages", columnDefinition = "text")
    private String llmMessages;
}
