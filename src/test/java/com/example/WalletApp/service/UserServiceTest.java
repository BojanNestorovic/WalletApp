package com.example.WalletApp.service;

import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private Currency testCurrency;

    @BeforeEach
    void setUp() {
        testCurrency = new Currency("USD", 1.0);
        testCurrency.setId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("password123");
        testUser.setBirthDate(new Date());
        testUser.setCurrency(testCurrency);
    }

    @Test
    void testRegisterUser_Success() {
        // Given
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.registerUser(testUser);

        // Then
        assertNotNull(result);
        assertEquals("johndoe", result.getUsername());
        verify(userRepository).save(testUser);
    }

    @Test
    void testRegisterUser_UsernameExists() {
        // Given
        when(userRepository.existsByUsername("johndoe")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(testUser);
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmailExists() {
        // Given
        when(userRepository.existsByUsername("johndoe")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser(testUser);
        });

        assertEquals("Email already exists", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testValidateUserCredentials_Valid() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validateUserCredentials("johndoe", "password123");

        // Then
        assertTrue(result);
    }

    @Test
    void testValidateUserCredentials_InvalidPassword() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.validateUserCredentials("johndoe", "wrongpassword");

        // Then
        assertFalse(result);
    }

    @Test
    void testValidateUserCredentials_UserNotFound() {
        // Given
        when(userRepository.findByUsername("johndoe")).thenReturn(Optional.empty());

        // When
        boolean result = userService.validateUserCredentials("johndoe", "password123");

        // Then
        assertFalse(result);
    }

    @Test
    void testGetUserById_Found() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
    }

    @Test
    void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testBlockUser() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.blockUser(1L);

        // Then
        assertTrue(testUser.isBlocked());
        verify(userRepository).save(testUser);
    }

    @Test
    void testUnblockUser() {
        // Given
        testUser.setBlocked(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.unblockUser(1L);

        // Then
        assertFalse(testUser.isBlocked());
        verify(userRepository).save(testUser);
    }
}
