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
 * Утилита для создания и проверки JWT-токенов.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private Key signingKey;

    /**
     * Инициализирует ключ подписи после загрузки конфигурации.
     */
    @PostConstruct
    void init() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Создаёт токен для пользователя и добавляет дополнительные параметры.
     *
     * @param user        данные пользователя
     * @param extraClaims дополнительные claim-значения
     * @return строковое представление JWT
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
     * Создаёт токен без дополнительных параметров.
     *
     * @param user данные пользователя
     * @return JWT-токен
     */
    public String generateToken(UserDetails user) {
        return generateToken(user, Map.of());
    }

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Проверяет соответствие токена пользователю и срок его действия.
     *
     * @param token JWT-токен
     * @param user  пользовательские данные
     * @return {@code true}, если токен действителен
     */
    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    /**
     * Извлекает произвольный параметр из токена.
     *
     * @param token    JWT-токен
     * @param resolver функция получения значения из {@link Claims}
     * @param <T>      тип возвращаемого значения
     * @return извлечённое значение
     */
    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Проверяет истечение срока действия токена.
     *
     * @param token JWT-токен
     * @return {@code true}, если токен просрочен
     */
    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    /**
     * Расшифровывает токен и возвращает все claims.
     *
     * @param token JWT-токен
     * @return объект {@link Claims} с данными токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
