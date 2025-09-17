package com.example.WalletApp.service;

import com.example.WalletApp.entity.SavingsGoal;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.SavingsGoalRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import com.example.WalletApp.service.impl.SavingsGoalServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingsGoalServiceTest {

    @Mock
    private SavingsGoalRepository savingsGoalRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private SavingsGoalServiceImpl savingsGoalService;

    private SavingsGoal testSavingsGoal;
    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setName("Test Wallet");

        testSavingsGoal = new SavingsGoal();
        testSavingsGoal.setId(1L);
        testSavingsGoal.setName("Vacation Fund");
        testSavingsGoal.setTargetAmount(new BigDecimal("5000.00"));
        testSavingsGoal.setCurrentAmount(new BigDecimal("1000.00"));
        testSavingsGoal.setTargetDate(new Date(System.currentTimeMillis() + 86400000)); // Tomorrow
        testSavingsGoal.setUser(testUser);
        testSavingsGoal.setWallet(testWallet);
        testSavingsGoal.setCompleted(false);
    }

    @Test
    void testCreateSavingsGoal_Success() {
        // Given
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(testSavingsGoal);

        // When
        SavingsGoal result = savingsGoalService.createSavingsGoal(testSavingsGoal);

        // Then
        assertNotNull(result);
        assertEquals("Vacation Fund", result.getName());
        assertEquals(new BigDecimal("5000.00"), result.getTargetAmount());
        verify(savingsGoalRepository).save(testSavingsGoal);
    }

    @Test
    void testUpdateSavingsGoal_Success() {
        // Given
        SavingsGoal updatedGoal = new SavingsGoal();
        updatedGoal.setName("Updated Vacation Fund");
        updatedGoal.setTargetAmount(new BigDecimal("6000.00"));
        updatedGoal.setTargetDate(new Date());

        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(testSavingsGoal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(testSavingsGoal);

        // When
        SavingsGoal result = savingsGoalService.updateSavingsGoal(1L, updatedGoal);

        // Then
        assertNotNull(result);
        verify(savingsGoalRepository).save(testSavingsGoal);
    }

    @Test
    void testUpdateSavingsGoal_GoalNotFound() {
        // Given
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            savingsGoalService.updateSavingsGoal(1L, testSavingsGoal);
        });

        assertEquals("Savings goal not found with id: 1", exception.getMessage());
        verify(savingsGoalRepository, never()).save(any(SavingsGoal.class));
    }

    @Test
    void testDeleteSavingsGoal_Success() {
        // Given
        when(savingsGoalRepository.existsById(1L)).thenReturn(true);

        // When
        savingsGoalService.deleteSavingsGoal(1L);

        // Then
        verify(savingsGoalRepository).deleteById(1L);
    }

    @Test
    void testDeleteSavingsGoal_GoalNotFound() {
        // Given
        when(savingsGoalRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            savingsGoalService.deleteSavingsGoal(1L);
        });

        assertEquals("Savings goal not found with id: 1", exception.getMessage());
        verify(savingsGoalRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetSavingsGoalById_Found() {
        // Given
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(testSavingsGoal));

        // When
        Optional<SavingsGoal> result = savingsGoalService.getSavingsGoalById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testSavingsGoal, result.get());
    }

    @Test
    void testGetSavingsGoalById_NotFound() {
        // Given
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<SavingsGoal> result = savingsGoalService.getSavingsGoalById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetSavingsGoalsByUserId() {
        // Given
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserId(1L)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getSavingsGoalsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testSavingsGoal, result.get(0));
    }

    @Test
    void testGetSavingsGoalsByWalletId() {
        // Given
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByWalletId(1L)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getSavingsGoalsByWalletId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testSavingsGoal, result.get(0));
    }

    @Test
    void testGetCompletedSavingsGoals() {
        // Given
        testSavingsGoal.setCompleted(true);
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserIdAndCompleted(1L, true)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getCompletedSavingsGoals(1L);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isCompleted());
    }

    @Test
    void testGetActiveSavingsGoals() {
        // Given
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserIdAndCompleted(1L, false)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getActiveSavingsGoals(1L);

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isCompleted());
    }

    @Test
    void testAddToSavingsGoal_Success() {
        // Given
        BigDecimal amount = new BigDecimal("500.00");
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(testSavingsGoal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(testSavingsGoal);

        // When
        savingsGoalService.addToSavingsGoal(1L, amount);

        // Then
        assertEquals(new BigDecimal("1500.00"), testSavingsGoal.getCurrentAmount());
        verify(savingsGoalRepository).save(testSavingsGoal);
    }

    @Test
    void testAddToSavingsGoal_GoalNotFound() {
        // Given
        BigDecimal amount = new BigDecimal("500.00");
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            savingsGoalService.addToSavingsGoal(1L, amount);
        });

        assertEquals("Savings goal not found with id: 1", exception.getMessage());
        verify(savingsGoalRepository, never()).save(any(SavingsGoal.class));
    }

    @Test
    void testAddToSavingsGoal_CompleteGoal() {
        // Given
        BigDecimal amount = new BigDecimal("4000.00"); // This will complete the goal
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(testSavingsGoal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(testSavingsGoal);

        // When
        savingsGoalService.addToSavingsGoal(1L, amount);

        // Then
        assertEquals(new BigDecimal("5000.00"), testSavingsGoal.getCurrentAmount());
        assertTrue(testSavingsGoal.isCompleted());
        verify(savingsGoalRepository).save(testSavingsGoal);
    }

    @Test
    void testCompleteSavingsGoal_Success() {
        // Given
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.of(testSavingsGoal));
        when(savingsGoalRepository.save(any(SavingsGoal.class))).thenReturn(testSavingsGoal);

        // When
        savingsGoalService.completeSavingsGoal(1L);

        // Then
        assertTrue(testSavingsGoal.isCompleted());
        verify(savingsGoalRepository).save(testSavingsGoal);
    }

    @Test
    void testCompleteSavingsGoal_GoalNotFound() {
        // Given
        when(savingsGoalRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            savingsGoalService.completeSavingsGoal(1L);
        });

        assertEquals("Savings goal not found with id: 1", exception.getMessage());
        verify(savingsGoalRepository, never()).save(any(SavingsGoal.class));
    }

    @Test
    void testGetOverdueGoals() {
        // Given
        testSavingsGoal.setTargetDate(new Date(System.currentTimeMillis() - 86400000)); // Yesterday
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserId(1L)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getOverdueGoals(1L);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isOverdue());
    }

    @Test
    void testGetGoalsByDateRange() {
        // Given
        Date startDate = new Date();
        Date endDate = new Date();
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserIdAndTargetDateBetween(1L, startDate, endDate)).thenReturn(goals);

        // When
        List<SavingsGoal> result = savingsGoalService.getGoalsByDateRange(1L, startDate, endDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(testSavingsGoal, result.get(0));
    }

    @Test
    void testGetTotalSavingsByUserId() {
        // Given
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserId(1L)).thenReturn(goals);

        // When
        BigDecimal result = savingsGoalService.getTotalSavingsByUserId(1L);

        // Then
        assertEquals(new BigDecimal("1000.00"), result);
    }

    @Test
    void testGetTotalTargetAmountByUserId() {
        // Given
        List<SavingsGoal> goals = Arrays.asList(testSavingsGoal);
        when(savingsGoalRepository.findByUserId(1L)).thenReturn(goals);

        // When
        BigDecimal result = savingsGoalService.getTotalTargetAmountByUserId(1L);

        // Then
        assertEquals(new BigDecimal("5000.00"), result);
    }
}
