package com.example.economicssimulatorserver.entity;

import com.example.economicssimulatorserver.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.Instant;

/**
 * Entity representing a refresh token for user session management.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(columnList = "token", unique = true),
        @Index(columnList = "user_id")
})
public class RefreshToken {

    /**
     * Unique identifier of the refresh token.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Token string.
     */
    @Column(nullable = false, unique = true, length = 256)
    private String token;

    /**
     * User to whom this refresh token belongs.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Expiration timestamp of the token.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}
