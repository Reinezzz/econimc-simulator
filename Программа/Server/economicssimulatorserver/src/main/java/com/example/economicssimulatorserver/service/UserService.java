package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    /* ---------- CRUD‑утилиты ---------- */

    @Transactional
    public User register(String username, String email, String rawPassword) {
        if (userRepo.existsByUsername(username))
            throw new IllegalArgumentException("Username already taken");
        if (userRepo.existsByEmail(email))
            throw new IllegalArgumentException("Email already registered");

        User user = User.builder()
                .username(username)
                .email(email)
                .passwordHash(encoder.encode(rawPassword))
                .enabled(false)
                .build();
        return userRepo.save(user);
    }

    @Transactional
    public void enableUser(User user) {
        user.setEnabled(true);
        userRepo.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepo.findByEmail(email);
    }

    public Optional<User> findByUsernameOrEmail(String login) {
        return userRepo.findByUsernameOrEmail(login, login);
    }

    /* ---------- UserDetailsService для Spring Security ---------- */

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {

        User user = findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        /* Конструируем Spring‑овский UserDetails без ролей */
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .disabled(!user.isEnabled())            // если e‑mail не подтверждён
                .authorities(Collections.emptyList())   // ролей нет
                .build();
    }

}
