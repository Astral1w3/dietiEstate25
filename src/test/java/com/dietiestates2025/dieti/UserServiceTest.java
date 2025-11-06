package com.dietiestates2025.dieti;
import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void changeUserPassword_Success() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("test@example.com", "oldPassword", "newPassword");
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.changeUserPassword(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changeUserPassword_UserNotFound() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("nonexistent@example.com", "oldPassword", "newPassword");
        when(userRepository.findById("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.changeUserPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserPassword_IncorrectCurrentPassword() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("test@example.com", "wrongPassword", "newPassword");
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedOldPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(BadCredentialsException.class, () -> userService.changeUserPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserPassword_InvalidNewPassword_Null() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("test@example.com", "oldPassword", null);
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.changeUserPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserPassword_InvalidNewPassword_Blank() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("test@example.com", "oldPassword", " ");
        User user = new User();
        user.setEmail("test@example.com");
        user.setUserPassword("encodedOldPassword");

        when(userRepository.findById("test@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", "encodedOldPassword")).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.changeUserPassword(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserPassword_ForSocialLoginUser_Success() {
        // Arrange
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO("social@example.com", null, "newPassword");
        User user = new User();
        user.setEmail("social@example.com");
        user.setUserPassword(null); // No existing password

        when(userRepository.findById("social@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        // Act
        userService.changeUserPassword(request);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, never()).matches(any(), any());
    }
}