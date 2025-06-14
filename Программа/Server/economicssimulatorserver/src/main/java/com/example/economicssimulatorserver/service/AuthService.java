package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.dto.*;
import com.example.economicssimulatorserver.entity.RefreshToken;
import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.util.JwtUtil;
import com.example.economicssimulatorserver.exception.LocalizedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

/**
 * Сервис аутентификации и управления пользователями.
 * Отвечает за регистрацию, вход, сброс пароля и работу с токенами.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final TokenService tokenService;
    private final MailService mailService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final CacheManager cacheManager;
    private final PasswordEncoder encoder;
    private final UserRepository userRepo;

    private static final String REG_CACHE = "registrations";

    /**
     * Регистрирует нового пользователя и отправляет код подтверждения на почту.
     *
     * @param req данные регистрации пользователя
     * @return результат операции
     */
    @Transactional
    public ApiResponse register(RegistrationRequest req) {
        if (userRepo.existsByEmail(req.email()))
            throw new LocalizedException("error.email_taken");
        if (userRepo.existsByUsername(req.username()))
            throw new LocalizedException("error.username_taken");

        var cache = cacheManager.getCache(REG_CACHE);
        if (cache.get(req.email()) != null) {
            cache.evict(req.email());
            throw new LocalizedException("error.registration_is_in_progress");
        }

        String code = String.format("%06d", (int) (Math.random() * 1_000_000));
        Instant expires = Instant.now().plusSeconds(15 * 60);

        PendingRegistration pending = new PendingRegistration(
                req.username(),
                req.email(),
                encoder.encode(req.password()),
                code,
                expires
        );

        cache.put(req.email(), pending);
        mailService.sendVerificationEmail(req.email(), req.username(), code);

        return new ApiResponse(true, "msg.verification_code_sent");
    }

    /**
     * Подтверждает электронную почту пользователя по коду из письма.
     *
     * @param req запрос с адресом и кодом подтверждения
     * @return результат операции
     */
    @Transactional
    public ApiResponse verifyEmail(VerificationRequest req) {
        var cache = cacheManager.getCache(REG_CACHE);
        PendingRegistration pending = cache.get(req.email(), PendingRegistration.class);

        if (pending == null)
            throw new LocalizedException("error.no_registration");

        if (pending.expiresAt.isBefore(Instant.now())) {
            cache.evict(req.email());
            throw new LocalizedException("error.verification_code_expired");
        }

        if (!pending.code.equals(req.code())) {
            cache.evict(req.email());
            throw new LocalizedException("error.wrong_verification_code");
        }

        userService.register(
                pending.username,
                pending.email,
                pending.passwordHash
        );

        cache.evict(req.email());
        return new ApiResponse(true, "msg.email_confirmed");
    }

    /**
     * Производит вход пользователя и выдаёт пару токенов.
     *
     * @param req логин и пароль пользователя
     * @return токен доступа и refresh-токен
     */
    public LoginResponse login(LoginRequest req) {
        Optional<User> userOpt = userRepo.findByUsernameOrEmail(req.usernameOrEmail(), req.usernameOrEmail());
        if (userOpt.isEmpty()) {
            throw new LocalizedException("error.user_not_found");
        }
        try {
            var authToken = new UsernamePasswordAuthenticationToken(
                    req.usernameOrEmail(), req.password());
            var auth = authManager.authenticate(authToken);
            UserDetails userDetails = (UserDetails) auth.getPrincipal();

            String access = jwtUtil.generateToken(userDetails);
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails);

            return new LoginResponse(access, refreshToken.getToken(), "Bearer");
        } catch (AuthenticationException ex) {
            throw new LocalizedException("error.wrong_credentials");
        }
    }

    /**
     * Начинает процедуру сброса пароля и отправляет код на почту.
     *
     * @param req запрос с адресом пользователя
     * @return результат операции
     */
    @Transactional
    public ApiResponse initiatePasswordReset(PasswordResetRequest req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new LocalizedException("error.account_not_found"));

        String code = tokenService.createPasswordResetToken(user);

        mailService.sendPasswordResetEmail(
                user.getEmail(),
                user.getUsername(),
                code);

        return new ApiResponse(true, "msg.password_rest_code_sent");
    }

    /**
     * Подтверждает сброс пароля по коду и устанавливает новый пароль.
     *
     * @param req параметры с email, кодом и новым паролем
     * @return результат операции
     */
    @Transactional
    public ApiResponse confirmPasswordReset(PasswordResetConfirm req) {
        User user = userService.findByEmail(req.email())
                .orElseThrow(() -> new LocalizedException("error.account_not_found"));
        boolean ok = tokenService.validatePasswordResetCode(user, req.code());
        if (!ok) {
            throw new LocalizedException("error.wrong_password_reset_code");
        }

        userService.updatePassword(user, req.newPassword());
        tokenService.evictPasswordResetToken(user);

        return new ApiResponse(true, "msg.password_updated");
    }

    /**
     * Отменяет процедуру сброса пароля для указанного пользователя.
     *
     * @param email адрес электронной почты
     */
    public void cancelPasswordReset(String email) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new LocalizedException("error.email_not_found"));
        tokenService.evictPasswordResetToken(user);
    }

    /**
     * Обновляет токен доступа по действующему refresh-токену.
     *
     * @param request запрос, содержащий refresh-токен
     * @return новые токены доступа и обновления
     */
    public RefreshTokenResponse refreshTokens(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(request.refreshToken());
        User user = refreshToken.getUser();

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPasswordHash())
                .authorities("USER")
                .build();

        String accessToken = jwtUtil.generateToken(userDetails);

        return new RefreshTokenResponse(accessToken, refreshToken.getToken());
    }

    /**
     * Завершает сессию пользователя и удаляет refresh-токен.
     *
     * @param request содержит токен для удаления
     */
    public void logout(LogoutRequest request) {
        refreshTokenService.deleteByToken(request.refreshToken());
    }
}
