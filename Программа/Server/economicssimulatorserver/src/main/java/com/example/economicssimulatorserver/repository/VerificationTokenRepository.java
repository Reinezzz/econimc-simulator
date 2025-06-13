package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.VerificationToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий токенов подтверждения электронной почты пользователей.
 */
@Repository
public interface VerificationTokenRepository
        extends JpaRepository<VerificationToken, Long> {

    /**
     * Находит токен подтверждения по его коду.
     *
     * @param code значение токена
     * @return сущность токена, если найдена
     */
    Optional<VerificationToken> findByCode(String code);

    /**
     * Получает токен подтверждения, связанный с пользователем.
     *
     * @param user пользователь для поиска
     * @return сущность токена, если найдена
     */
    Optional<VerificationToken> findByUser(User user);

    /**
     * Удаляет токены подтверждения, связанные с пользователем.
     *
     * @param user пользователь, чьи токены нужно удалить
     */
    void deleteByUser(User user);
}