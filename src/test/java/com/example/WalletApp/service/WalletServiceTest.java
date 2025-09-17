package com.example.WalletApp.service;

import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import com.example.WalletApp.service.impl.WalletServiceImpl;
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
class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private WalletServiceImpl walletService;

    private Wallet testWallet;
    private User testUser;
    private Currency testCurrency;

    @BeforeEach
    void setUp() {
        testCurrency = new Currency("USD", 1.0);
        testCurrency.setId(1L);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");

        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setName("Test Wallet");
        testWallet.setInitialBalance(new BigDecimal("1000.00"));
        testWallet.setCurrentBalance(new BigDecimal("1000.00"));
        testWallet.setUser(testUser);
        testWallet.setCurrency(testCurrency);
        testWallet.setSavings(false);
        testWallet.setArchived(false);
    }

    @Test
    void testCreateWallet_Success() {
        // Given
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        Wallet result = walletService.createWallet(testWallet);

        // Then
        assertNotNull(result);
        assertEquals("Test Wallet", result.getName());
        assertEquals(new BigDecimal("1000.00"), result.getCurrentBalance());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testUpdateWallet_Success() {
        // Given
        Wallet updatedWallet = new Wallet();
        updatedWallet.setName("Updated Wallet");
        updatedWallet.setSavings(true);
        updatedWallet.setCurrency(testCurrency);

        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        Wallet result = walletService.updateWallet(1L, updatedWallet);

        // Then
        assertNotNull(result);
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testUpdateWallet_WalletNotFound() {
        // Given
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.updateWallet(1L, testWallet);
        });

        assertEquals("Wallet not found with id: 1", exception.getMessage());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testDeleteWallet_Success() {
        // Given
        when(walletRepository.existsById(1L)).thenReturn(true);

        // When
        walletService.deleteWallet(1L);

        // Then
        verify(walletRepository).deleteById(1L);
    }

    @Test
    void testDeleteWallet_WalletNotFound() {
        // Given
        when(walletRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.deleteWallet(1L);
        });

        assertEquals("Wallet not found with id: 1", exception.getMessage());
        verify(walletRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetWalletById_Found() {
        // Given
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));

        // When
        Optional<Wallet> result = walletService.getWalletById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testWallet, result.get());
    }

    @Test
    void testGetWalletById_NotFound() {
        // Given
        when(walletRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Wallet> result = walletService.getWalletById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetWalletsByUserId() {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletRepository.findByUserId(1L)).thenReturn(wallets);

        // When
        List<Wallet> result = walletService.getWalletsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testWallet, result.get(0));
    }

    @Test
    void testGetActiveWalletsByUserId() {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletRepository.findActiveWalletsByUserId(1L)).thenReturn(wallets);

        // When
        List<Wallet> result = walletService.getActiveWalletsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testWallet, result.get(0));
    }

    @Test
    void testGetSavingsWalletsByUserId() {
        // Given
        testWallet.setSavings(true);
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletRepository.findByUserIdAndSavings(1L, true)).thenReturn(wallets);

        // When
        List<Wallet> result = walletService.getSavingsWalletsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isSavings());
    }

    @Test
    void testGetTotalBalanceByUserId() {
        // Given
        BigDecimal totalBalance = new BigDecimal("1500.00");
        when(walletRepository.getTotalBalanceByUserId(1L)).thenReturn(totalBalance);

        // When
        BigDecimal result = walletService.getTotalBalanceByUserId(1L);

        // Then
        assertEquals(totalBalance, result);
    }

    @Test
    void testGetTotalBalanceByUserId_Null() {
        // Given
        when(walletRepository.getTotalBalanceByUserId(1L)).thenReturn(null);

        // When
        BigDecimal result = walletService.getTotalBalanceByUserId(1L);

        // Then
        assertEquals(BigDecimal.ZERO, result);
    }

    @Test
    void testArchiveWallet_Success() {
        // Given
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        walletService.archiveWallet(1L);

        // Then
        assertTrue(testWallet.isArchived());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testUnarchiveWallet_Success() {
        // Given
        testWallet.setArchived(true);
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        walletService.unarchiveWallet(1L);

        // Then
        assertFalse(testWallet.isArchived());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testAddToWalletBalance_Success() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        walletService.addToWalletBalance(1L, amount);

        // Then
        assertEquals(new BigDecimal("1100.00"), testWallet.getCurrentBalance());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testSubtractFromWalletBalance_Success() {
        // Given
        BigDecimal amount = new BigDecimal("100.00");
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        // When
        walletService.subtractFromWalletBalance(1L, amount);

        // Then
        assertEquals(new BigDecimal("900.00"), testWallet.getCurrentBalance());
        verify(walletRepository).save(testWallet);
    }

    @Test
    void testSubtractFromWalletBalance_InsufficientFunds() {
        // Given
        BigDecimal amount = new BigDecimal("1500.00");
        when(walletRepository.findById(1L)).thenReturn(Optional.of(testWallet));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            walletService.subtractFromWalletBalance(1L, amount);
        });

        assertEquals("Insufficient balance in wallet: 1", exception.getMessage());
        verify(walletRepository, never()).save(any(Wallet.class));
    }

    @Test
    void testGetAverageWalletBalance() {
        // Given
        BigDecimal average = new BigDecimal("500.00");
        when(walletRepository.getAverageWalletBalance()).thenReturn(average);

        // When
        BigDecimal result = walletService.getAverageWalletBalance();

        // Then
        assertEquals(average, result);
    }

    @Test
    void testGetTotalSystemBalance() {
        // Given
        BigDecimal total = new BigDecimal("10000.00");
        when(walletRepository.getTotalSystemBalance()).thenReturn(total);

        // When
        BigDecimal result = walletService.getTotalSystemBalance();

        // Then
        assertEquals(total, result);
    }

    @Test
    void testGetWalletsByCurrency() {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletRepository.findByCurrencyId(1L)).thenReturn(wallets);

        // When
        List<Wallet> result = walletService.getWalletsByCurrency(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testWallet, result.get(0));
    }
}
