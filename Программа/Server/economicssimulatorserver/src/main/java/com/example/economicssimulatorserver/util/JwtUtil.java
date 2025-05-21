package com.example.economicssimulatorserver.util;

import com.example.economicssimulatorserver.config.JwtConfig;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * Утилитный компонент для создания, валидации и парсинга JWT access-токенов.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private Key signingKey;

    /**
     * Инициализация: формирует секретный ключ из Base64.
     */
    @PostConstruct
    private void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создает access-токен с пользовательскими claims (опционально).
     *
     * @param user        пользователь (UserDetails)
     * @param extraClaims дополнительные claims для токена
     * @return JWT access-токен
     */
    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(jwtConfig.getAccessTokenExpirationMinutes() * 60);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(signingKey)
                .compact();
    }

    /**
     * Создает access-токен без дополнительных claims.
     *
     * @param user пользователь (UserDetails)
     * @return JWT access-токен
     */
    public String generateToken(UserDetails user) {
        return generateToken(user, Map.of());
    }

    /**
     * Извлекает имя пользователя (subject) из токена.
     *
     * @param token JWT access-токен
     * @return имя пользователя (логин/email)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Проверяет валидность токена для заданного пользователя.
     *
     * @param token JWT токен
     * @param user  объект пользователя
     * @return true, если токен валиден и не истек
     */
    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    // --- Внутренние методы ---

    /**
     * Извлекает определенный claim из токена с помощью resolver-функции.
     */
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Проверяет, истек ли срок действия токена.
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Извлекает все claims из токена.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
