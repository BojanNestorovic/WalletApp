package com.example.WalletApp.service;

import com.example.WalletApp.entity.*;
import com.example.WalletApp.repository.*;
import com.example.WalletApp.service.impl.TransactionServiceImpl;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction testTransaction;
    private User testUser;
    private Wallet testWallet;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setName("Test Wallet");
        testWallet.setCurrentBalance(new BigDecimal("1000.00"));

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setType(CategoryType.EXPENSE);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setName("Test Transaction");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setType(TransactionType.EXPENSE);
        testTransaction.setCategory(testCategory);
        testTransaction.setWallet(testWallet);
        testTransaction.setUser(testUser);
        testTransaction.setRepeating(false);
    }

    @Test
    void testCreateTransaction_Success() {
        // Given
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionService.createTransaction(testTransaction);

        // Then
        assertNotNull(result);
        assertEquals("Test Transaction", result.getName());
        assertEquals(new BigDecimal("100.00"), result.getAmount());
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void testUpdateTransaction_Success() {
        // Given
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setName("Updated Transaction");
        updatedTransaction.setAmount(new BigDecimal("150.00"));
        updatedTransaction.setType(TransactionType.INCOME);
        updatedTransaction.setCategory(testCategory);

        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // When
        Transaction result = transactionService.updateTransaction(1L, updatedTransaction);

        // Then
        assertNotNull(result);
        verify(transactionRepository).save(testTransaction);
    }

    @Test
    void testUpdateTransaction_TransactionNotFound() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.updateTransaction(1L, testTransaction);
        });

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void testDeleteTransaction_Success() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When
        transactionService.deleteTransaction(1L);

        // Then
        verify(transactionRepository).deleteById(1L);
    }

    @Test
    void testDeleteTransaction_TransactionNotFound() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.deleteTransaction(1L);
        });

        assertEquals("Transaction not found with id: 1", exception.getMessage());
        verify(transactionRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetTransactionById_Found() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.of(testTransaction));

        // When
        Optional<Transaction> result = transactionService.getTransactionById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testTransaction, result.get());
    }

    @Test
    void testGetTransactionById_NotFound() {
        // Given
        when(transactionRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Transaction> result = transactionService.getTransactionById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetTransactionsByUserId() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUserId(1L)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTransactionsByWalletId() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByWalletId(1L)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByWalletId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTransactionsByCategoryId() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByCategoryId(1L)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByCategoryId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTransactionsByType_Success() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByType(TransactionType.EXPENSE)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByType("EXPENSE");

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTransactionsByType_InvalidType() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.getTransactionsByType("INVALID");
        });

        assertEquals("Invalid transaction type: INVALID", exception.getMessage());
    }

    @Test
    void testGetRepeatingTransactions() {
        // Given
        testTransaction.setRepeating(true);
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByRepeating(true)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getRepeatingTransactions();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isRepeating());
    }

    @Test
    void testGetTransactionsByDateRange() {
        // Given
        Date startDate = new Date();
        Date endDate = new Date();
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByDateOfTransactionBetween(startDate, endDate)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByDateRange(startDate, endDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTransactionsByUserAndDateRange() {
        // Given
        Date startDate = new Date();
        Date endDate = new Date();
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUserIdAndDateOfTransactionBetween(1L, startDate, endDate)).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.getTransactionsByUserAndDateRange(1L, startDate, endDate);

        // Then
        assertEquals(1, result.size());
        assertEquals(testTransaction, result.get(0));
    }

    @Test
    void testGetTotalIncomeByUserId() {
        // Given
        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setType(TransactionType.INCOME);
        incomeTransaction.setAmount(new BigDecimal("200.00"));

        List<Transaction> transactions = Arrays.asList(testTransaction, incomeTransaction);
        when(transactionRepository.findByUserId(1L)).thenReturn(transactions);

        // When
        double result = transactionService.getTotalIncomeByUserId(1L);

        // Then
        assertEquals(200.0, result);
    }

    @Test
    void testGetTotalExpenseByUserId() {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByUserId(1L)).thenReturn(transactions);

        // When
        double result = transactionService.getTotalExpenseByUserId(1L);

        // Then
        assertEquals(100.0, result);
    }

    @Test
    void testGetNetBalanceByUserId() {
        // Given
        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setType(TransactionType.INCOME);
        incomeTransaction.setAmount(new BigDecimal("200.00"));

        List<Transaction> transactions = Arrays.asList(testTransaction, incomeTransaction);
        when(transactionRepository.findByUserId(1L)).thenReturn(transactions);

        // When
        double result = transactionService.getNetBalanceByUserId(1L);

        // Then
        assertEquals(100.0, result); // 200 - 100 = 100
    }
}
