package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.exception.LocalizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

/**
 * Сервис управления пользователями и интеграции с Spring Security.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder encoder;

    /**
     * Регистрирует нового пользователя без подтверждения почты.
     */
    @Transactional
    public User register(String username, String email, String passwordHash) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordHash);
        user.setEnabled(true); // Email всегда подтвержден при создании
        return userRepo.save(user);
    }

    /**
     * Находит пользователя по электронной почте.
     */
    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    /**
     * Ищет пользователя по имени или email.
     */
    public Optional<User> findByUsernameOrEmail(String login) {
        return userRepo.findByUsernameOrEmail(login, login);
    }

    /**
     * Загружает данные пользователя для Spring Security.
     */
    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        User user = findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new LocalizedException("error.user_not_found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isEnabled())
                .authorities(Collections.emptyList())
                .build();
    }

    /**
     * Изменяет пароль пользователя.
     */
    @Transactional
    public void updatePassword(User user, String rawPassword) {
        user.setPasswordHash(encoder.encode(rawPassword));
        userRepo.save(user);
    }

}
