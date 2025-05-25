package com.example.economicssimulatorserver.service;

import com.example.economicssimulatorserver.entity.User;
import com.example.economicssimulatorserver.repository.UserRepository;
import com.example.economicssimulatorserver.exception.LocalizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepo;
    @Mock private org.springframework.security.crypto.password.PasswordEncoder encoder;
    @InjectMocks private UserService userService;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        userService = new UserService(userRepo, encoder);
//    }

    @Test
    void register_ShouldSaveUser() {
        User user = new User();
        when(userRepo.save(any(User.class))).thenReturn(user);

        User res = userService.register("user", "e@e.com", "hash");

        assertNotNull(res);
        verify(userRepo).save(any(User.class));
        assertTrue(res.isEnabled());
    }

    @Test
    void findByEmail_ShouldReturnUser_WhenExists() {
        User user = new User();
        when(userRepo.findByEmail("e@e.com")).thenReturn(Optional.of(user));
        assertTrue(userService.findByEmail("e@e.com").isPresent());
    }

    @Test
    void findByUsernameOrEmail_ShouldReturnUser_WhenExists() {
        User user = new User();
        when(userRepo.findByUsernameOrEmail("x", "x")).thenReturn(Optional.of(user));
        assertTrue(userService.findByUsernameOrEmail("x").isPresent());
    }

    @Test
    void loadUserByUsername_ShouldReturnUserDetails_WhenUserExists() {
        User user = new User();
        user.setUsername("user");
        user.setPasswordHash("pw");
        user.setEnabled(true);
        when(userRepo.findByUsernameOrEmail("user", "user")).thenReturn(Optional.of(user));

        UserDetails details = userService.loadUserByUsername("user");
        assertEquals("user", details.getUsername());
        assertEquals("pw", details.getPassword());
        assertTrue(details.isEnabled());
    }

    @Test
    void loadUserByUsername_ShouldThrow_WhenUserNotFound() {
        when(userRepo.findByUsernameOrEmail("no", "no")).thenReturn(Optional.empty());
        assertThrows(LocalizedException.class, () -> userService.loadUserByUsername("no"));
    }

    @Test
    void updatePassword_ShouldEncodeAndSave() {
        User user = new User();
        when(encoder.encode("raw")).thenReturn("hashed");
        when(userRepo.save(user)).thenReturn(user);

        userService.updatePassword(user, "raw");
        verify(userRepo).save(user);
        assertEquals("hashed", user.getPasswordHash());
    }
}
