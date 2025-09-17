package com.example.WalletApp.controller;

import com.example.WalletApp.config.TestSecurityConfig;
import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.entity.Category;
import com.example.WalletApp.entity.Transaction;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
@Import(TestSecurityConfig.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionDTO testTransactionDTO;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransactionDTO = new TransactionDTO();
        testTransactionDTO.setId(1L);
        testTransactionDTO.setName("Test Transaction");
        testTransactionDTO.setAmount(new BigDecimal("100.00"));
        testTransactionDTO.setType("EXPENSE");
        testTransactionDTO.setCategoryId(1L);
        testTransactionDTO.setWalletId(1L);
        testTransactionDTO.setUserId(1L);
        testTransactionDTO.setRepeating(false);

        testTransaction = new Transaction();
        testTransaction.setId(1L);
        testTransaction.setName("Test Transaction");
        testTransaction.setAmount(new BigDecimal("100.00"));
    }

    @Test
    void testGetAllTransactions_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getAllTransactions()).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetAllTransactionsWithPagination_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, PageRequest.of(0, 10), 1);
        when(transactionService.getAllTransactions(any())).thenReturn(transactionPage);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionById_Success() throws Exception {
        // Given
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.of(testTransaction));
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionById_NotFound() throws Exception {
        // Given
        when(transactionService.getTransactionById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/transactions/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateTransaction_Success() throws Exception {
        // Given
        when(transactionService.convertToEntity(any(TransactionDTO.class))).thenReturn(testTransaction);
        when(transactionService.createTransaction(any(Transaction.class))).thenReturn(testTransaction);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Transaction"));
    }

    @Test
    void testCreateTransaction_ValidationError() throws Exception {
        // Given
        testTransactionDTO.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateTransaction_Success() throws Exception {
        // Given
        when(transactionService.convertToEntity(any(TransactionDTO.class))).thenReturn(testTransaction);
        when(transactionService.updateTransaction(anyLong(), any(Transaction.class))).thenReturn(testTransaction);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(put("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testTransactionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Transaction"));
    }

    @Test
    void testDeleteTransaction_Success() throws Exception {
        // Given
        doNothing().when(transactionService).deleteTransaction(1L);

        // When & Then
        mockMvc.perform(delete("/api/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Transaction deleted successfully"));
    }

    @Test
    void testGetTransactionsByUserId_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByUserId(1L)).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionsByWalletId_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByWalletId(1L)).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/wallet/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionsByCategoryId_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByCategoryId(1L)).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/category/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionsByType_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByType("EXPENSE")).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/type/EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetRepeatingTransactions_Success() throws Exception {
        // Given
        testTransactionDTO.setRepeating(true);
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getRepeatingTransactions()).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/repeating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].repeating").value(true));
    }

    @Test
    void testGetTransactionsByDateRange_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByDateRange(any(Date.class), any(Date.class))).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/date-range")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionsByUserAndDateRange_Success() throws Exception {
        // Given
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(transactionService.getTransactionsByUserAndDateRange(anyLong(), any(Date.class), any(Date.class))).thenReturn(transactions);
        when(transactionService.convertToDTO(any(Transaction.class))).thenReturn(testTransactionDTO);

        // When & Then
        mockMvc.perform(get("/api/transactions/user/1/date-range")
                .param("startDate", "2023-01-01")
                .param("endDate", "2023-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Transaction"));
    }

    @Test
    void testGetTransactionStatsByUserId_Success() throws Exception {
        // Given
        when(transactionService.getTotalIncomeByUserId(1L)).thenReturn(1000.0);
        when(transactionService.getTotalExpenseByUserId(1L)).thenReturn(500.0);
        when(transactionService.getNetBalanceByUserId(1L)).thenReturn(500.0);

        // When & Then
        mockMvc.perform(get("/api/transactions/user/1/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total Income: 1000.00, Total Expense: 500.00, Net Balance: 500.00"));
    }
}
