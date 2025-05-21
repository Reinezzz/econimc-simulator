package com.example.economicssimulatorserver.config;

import com.example.economicssimulatorserver.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Основная конфигурация безопасности Spring Security для приложения:
 * <ul>
 *     <li>Stateless-сессии (без хранения сессий на сервере)</li>
 *     <li>JWT-аутентификация</li>
 *     <li>Разрешение анонимного доступа только к эндпоинтам /auth/**</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;

    /**
     * Получает стандартный {@link AuthenticationManager} из конфигурации Spring Security.
     *
     * @param configuration текущая конфигурация аутентификации
     * @return бин {@link AuthenticationManager}
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Основная цепочка фильтров безопасности приложения.
     * <p>
     * - Отключает CSRF<br>
     * - Разрешает CORS по умолчанию<br>
     * - Делает приложение stateless (без серверных сессий)<br>
     * - Разрешает только эндпоинты /auth/** без аутентификации<br>
     * - Вставляет фильтр JWT-аутентификации перед UsernamePasswordAuthenticationFilter
     *
     * @param http конфигуратор безопасности
     * @return настроенная цепочка {@link SecurityFilterChain}
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {}) // дефолтные CORS-настройки
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil, jwtConfig, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * JWT-фильтр (stateless): извлекает токен из заголовка, валидирует,
     * и при успешной валидации помещает {@link org.springframework.security.core.Authentication}
     * в {@link SecurityContextHolder}.
     */
    private static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final JwtConfig jwtConfig;
        private final UserDetailsService userDetailsService;

        public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                       JwtConfig jwtConfig,
                                       UserDetailsService userDetailsService) {
            this.jwtUtil = jwtUtil;
            this.jwtConfig = jwtConfig;
            this.userDetailsService = userDetailsService;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws ServletException, IOException {

            String authHeader = request.getHeader(jwtConfig.getHeader());
            String prefix = jwtConfig.getTokenPrefix();

            if (authHeader != null && authHeader.startsWith(prefix)) {
                String token = authHeader.substring(prefix.length());
                String username = jwtUtil.extractUsername(token);

                if (username != null &&
                        SecurityContextHolder.getContext().getAuthentication() == null) {

                    var userDetails = userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.isTokenValid(token, userDetails)) {
                        var authToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}
