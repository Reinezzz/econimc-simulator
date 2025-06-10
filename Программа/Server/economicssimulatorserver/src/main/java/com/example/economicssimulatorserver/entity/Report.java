package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.converter.LlmChatResponseDtoListConverter;
import com.example.economicssimulatorserver.converter.ModelParameterDtoListConverter;
import com.example.economicssimulatorserver.converter.ModelResultDtoConverter;
import com.example.economicssimulatorserver.dto.LlmChatResponseDto;
import com.example.economicssimulatorserver.dto.ModelParameterDto;
import com.example.economicssimulatorserver.dto.ModelResultDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

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
