package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA-репозиторий для управления пользователями.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователя по логину или email.
     */
    Optional<User> findByUsernameOrEmail(String username, String email);

    /**
     * Проверка существования пользователя по логину.
     */
    boolean existsByUsername(String username);

    /**
     * Проверка существования пользователя по email.
     */
    boolean existsByEmail(String email);
}
