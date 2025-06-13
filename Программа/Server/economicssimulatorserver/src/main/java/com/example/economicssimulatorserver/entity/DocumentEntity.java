package com.example.economicssimulatorserver.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Сущность, описывающая загруженный пользователем документ.
 */
@Builder
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "documents")
public class DocumentEntity {

    /**
     * Уникальный идентификатор документа.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Идентификатор пользователя, загрузившего документ.
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Название документа.
     */
    @Column(nullable = false)
    private String name;

    /**
     * Путь к файлу на сервере.
     */
    @Column(nullable = false, length = 1024)
    private String path;

    /**
     * Дата и время загрузки файла.
     */
    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt = LocalDateTime.now();

}
