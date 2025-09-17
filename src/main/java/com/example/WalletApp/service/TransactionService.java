package com.example.WalletApp.service;

import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    
    // CRUD Operations
    Transaction createTransaction(Transaction transaction);
    Transaction updateTransaction(Long id, Transaction transaction);
    void deleteTransaction(Long id);
    Optional<Transaction> getTransactionById(Long id);
    List<Transaction> getAllTransactions();
    Page<Transaction> getAllTransactions(Pageable pageable);
    
    // User-specific operations
    List<Transaction> getTransactionsByUserId(Long userId);
    List<Transaction> getTransactionsByWalletId(Long walletId);
    List<Transaction> getTransactionsByCategoryId(Long categoryId);
    
    // Transaction filtering
    List<Transaction> getTransactionsByType(String type);
    List<Transaction> getRepeatingTransactions();
    List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate);
    List<Transaction> getTransactionsByUserAndDateRange(Long userId, Date startDate, Date endDate);
    
    // Statistics
    double getTotalIncomeByUserId(Long userId);
    double getTotalExpenseByUserId(Long userId);
    double getNetBalanceByUserId(Long userId);
    
    // DTO Conversion
    TransactionDTO convertToDTO(Transaction transaction);
    Transaction convertToEntity(TransactionDTO transactionDTO);
}
