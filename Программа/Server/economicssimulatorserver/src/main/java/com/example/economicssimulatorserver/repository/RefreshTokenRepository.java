package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Репозиторий для работы с сущностями {@link com.example.economicssimulatorserver.entity.RefreshToken}.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Находит токен обновления по его строковому значению.
     *
     * @param token значение токена
     * @return токен обновления, если найден
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Возвращает токен обновления, связанный с указанным пользователем.
     *
     * @param user пользователь для поиска
     * @return токен обновления, если найден
     */
    Optional<RefreshToken> findByUser(User user);

    /**
     * Удаляет все токены обновления, принадлежащие пользователю.
     *
     * @param user пользователь, чьи токены нужно удалить
     * @return количество удалённых записей
     */
    long deleteByUser(User user);

    /**
     * Удаляет токен обновления по его значению.
     *
     * @param token значение токена
     * @return количество удалённых записей
     */
    @Transactional
    long deleteByToken(String token);
}