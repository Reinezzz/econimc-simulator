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
 * Утилита для создания и парсинга JWT.
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtConfig jwtConfig;
    private Key signingKey;

    @PostConstruct
    private void init() {
        // секрет хранится в Base64‑виде
        byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /* ---------- Публичное API ---------- */

    /** Создать access‑token c пользовательскими claims (опц.) */
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

    /** Перегрузка без дополнительных claims */
    public String generateToken(UserDetails user) {
        return generateToken(user, Map.of());
    }

    /** Имя (username/email) из токена */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /** Проверить валидность */
    public boolean isTokenValid(String token, UserDetails user) {
        String username = extractUsername(token);
        return username.equals(user.getUsername()) && !isTokenExpired(token);
    }

    /* ---------- Внутренние вспомогательные ---------- */

    private <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
