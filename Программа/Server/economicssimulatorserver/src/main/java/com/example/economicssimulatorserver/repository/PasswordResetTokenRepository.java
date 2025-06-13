package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.PasswordResetToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link com.example.economicssimulatorserver.entity.PasswordResetToken}.
 */
@Repository
public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Получает токен по его значению.
     *
     * @param code уникальный код токена
     * @return сущность токена, если найдена
     */
    Optional<PasswordResetToken> findByCode(String code);

    /**
     * Ищет токен, созданный для указанного пользователя.
     *
     * @param user пользователь, для которого создан токен
     * @return сущность токена, если найдена
     */
    Optional<PasswordResetToken> findByUser(User user);

    /**
     * Удаляет токены сброса пароля, связанные с пользователем.
     *
     * @param user пользователь, чьи токены необходимо удалить
     */
    void deleteByUser(User user);
}