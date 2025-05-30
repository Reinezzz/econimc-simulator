package com.example.economicssimulatorserver.repository;

import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for CRUD operations with RefreshToken entity.
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a refresh token by its token string.
     * @param token refresh token value
     * @return optional RefreshToken
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Deletes all refresh tokens belonging to a user.
     * @param user user entity
     * @return number of tokens deleted
     */
    long deleteByUser(User user);

    @Transactional
    long deleteByToken(String token); // <-- Вот это нужно!
}
