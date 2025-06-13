package com.example.economicssimulatorserver.config;

import com.example.economicssimulatorserver.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Конфигурация безопасности приложения.
 * Реализует настройку фильтров JWT и разрешений.
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
     * Предоставляет менеджер аутентификации из конфигурации Spring.
     *
     * @param configuration конфигурация аутентификации
     * @return менеджер аутентификации
     * @throws Exception в случае ошибок инициализации
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /**
     * Настраивает фильтры безопасности и правила доступа.
     *
     * @param http объект для конфигурации HTTP безопасности
     * @return цепочка фильтров безопасности
     * @throws Exception при ошибках конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
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
     * Фильтр проверки JWT для каждого запроса.
     */
    private static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;
        private final JwtConfig jwtConfig;
        private final UserDetailsService userDetailsService;

        /**
         * Создаёт фильтр аутентификации по JWT.
         *
         * @param jwtUtil утилита для работы с токенами
         * @param jwtConfig конфигурация JWT
         * @param userDetailsService сервис загрузки данных пользователей
         */
        public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                       JwtConfig jwtConfig,
                                       UserDetailsService userDetailsService) {
            this.jwtUtil = jwtUtil;
            this.jwtConfig = jwtConfig;
            this.userDetailsService = userDetailsService;
        }

        /**
         * Выполняет проверку JWT и устанавливает аутентификацию в контекст.
         *
         * @param request  входящий HTTP-запрос
         * @param response ответ HTTP
         * @param filterChain цепочка фильтров
         * @throws ServletException в случае ошибок фильтрации
         * @throws IOException при ошибках ввода-вывода
         */
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
