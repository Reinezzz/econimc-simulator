package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий пользователей приложения.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Ищет пользователя по имени пользователя.
     *
     * @param username уникальное имя входа
     * @return сущность пользователя, если найдена
     */
    Optional<User> findByUsername(String username);

    /**
     * Ищет пользователя по адресу электронной почты.
     *
     * @param email email пользователя
     * @return сущность пользователя, если найдена
     */
    Optional<User> findByEmail(String email);

    /**
     * Находит пользователя по имени пользователя или email.
     *
     * @param username имя пользователя для поиска
     * @param email    email для поиска
     * @return сущность пользователя, если найдена
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Проверяет, занято ли имя пользователя.
     *
     * @param username имя пользователя для проверки
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);

    /**
     * Проверяет, используется ли указанный email.
     *
     * @param email email для проверки
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);
}