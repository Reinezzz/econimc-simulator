package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.DocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий для работы с сущностями {@link com.example.economicssimulatorserver.entity.DocumentEntity}.
 */
@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long> {

    /**
     * Возвращает документы, принадлежащие указанному пользователю.
     *
     * @param userId идентификатор пользователя
     * @return список документов пользователя
     */
    List<DocumentEntity> findByUserId(Long userId);
}