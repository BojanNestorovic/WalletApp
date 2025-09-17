package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.entity.*;
import com.example.WalletApp.repository.*;
import com.example.WalletApp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionServiceImpl implements TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Override
    public Transaction createTransaction(Transaction transaction) {
        // Update wallet balance when creating transaction
        transaction.updateWalletBalance();
        return transactionRepository.save(transaction);
    }
    
    @Override
    public Transaction updateTransaction(Long id, Transaction transaction) {
        Transaction existingTransaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        
        // Revert old transaction's effect on wallet
        if (existingTransaction.getWallet() != null) {
            if (existingTransaction.isIncome()) {
                existingTransaction.getWallet().updateBalance(existingTransaction.getAmount().negate());
            } else {
                existingTransaction.getWallet().updateBalance(existingTransaction.getAmount());
            }
        }
        
        // Update transaction fields
        existingTransaction.setName(transaction.getName());
        existingTransaction.setAmount(transaction.getAmount());
        existingTransaction.setType(transaction.getType());
        existingTransaction.setCategory(transaction.getCategory());
        existingTransaction.setRepeating(transaction.isRepeating());
        existingTransaction.setFrequency(transaction.getFrequency());
        
        // Apply new transaction's effect on wallet
        existingTransaction.updateWalletBalance();
        
        return transactionRepository.save(existingTransaction);
    }
    
    @Override
    public void deleteTransaction(Long id) {
        Transaction transaction = transactionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Transaction not found with id: " + id));
        
        // Revert transaction's effect on wallet
        if (transaction.getWallet() != null) {
            if (transaction.isIncome()) {
                transaction.getWallet().updateBalance(transaction.getAmount().negate());
            } else {
                transaction.getWallet().updateBalance(transaction.getAmount());
            }
        }
        
        transactionRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Transaction> getAllTransactions(Pageable pageable) {
        return transactionRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserId(Long userId) {
        return transactionRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByWalletId(Long walletId) {
        return transactionRepository.findByWalletId(walletId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByCategoryId(Long categoryId) {
        return transactionRepository.findByCategoryId(categoryId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(String type) {
        try {
            TransactionType transactionType = TransactionType.valueOf(type.toUpperCase());
            return transactionRepository.findByType(transactionType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid transaction type: " + type);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getRepeatingTransactions() {
        return transactionRepository.findByRepeating(true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) {
        return transactionRepository.findByDateOfTransactionBetween(startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUserAndDateRange(Long userId, Date startDate, Date endDate) {
        return transactionRepository.findByUserIdAndDateOfTransactionBetween(userId, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getTotalIncomeByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
            .filter(Transaction::isIncome)
            .mapToDouble(t -> t.getAmount().doubleValue())
            .sum();
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getTotalExpenseByUserId(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
            .filter(Transaction::isExpense)
            .mapToDouble(t -> t.getAmount().doubleValue())
            .sum();
    }
    
    @Override
    @Transactional(readOnly = true)
    public double getNetBalanceByUserId(Long userId) {
        return getTotalIncomeByUserId(userId) - getTotalExpenseByUserId(userId);
    }
    
    @Override
    public TransactionDTO convertToDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setName(transaction.getName());
        dto.setAmount(transaction.getAmount());
        dto.setType(transaction.getType() != null ? transaction.getType().toString() : null);
        dto.setDateOfTransaction(transaction.getDateOfTransaction());
        dto.setRepeating(transaction.isRepeating());
        dto.setFrequency(transaction.getFrequency() != null ? transaction.getFrequency().toString() : null);
        
        if (transaction.getCategory() != null) {
            dto.setCategoryId(transaction.getCategory().getId());
        }
        
        if (transaction.getWallet() != null) {
            dto.setWalletId(transaction.getWallet().getId());
        }
        
        if (transaction.getUser() != null) {
            dto.setUserId(transaction.getUser().getId());
        }
        
        return dto;
    }
    
    @Override
    public Transaction convertToEntity(TransactionDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setId(dto.getId());
        transaction.setName(dto.getName());
        transaction.setAmount(dto.getAmount());
        transaction.setDateOfTransaction(dto.getDateOfTransaction());
        transaction.setRepeating(dto.isRepeating());
        
        if (dto.getType() != null) {
            try {
                transaction.setType(TransactionType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid transaction type: " + dto.getType());
            }
        }
        
        if (dto.getFrequency() != null) {
            try {
                transaction.setFrequency(Frequency.valueOf(dto.getFrequency().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid frequency: " + dto.getFrequency());
            }
        }
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId()).orElse(null);
            transaction.setCategory(category);
        }
        
        if (dto.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(dto.getWalletId()).orElse(null);
            transaction.setWallet(wallet);
        }
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            transaction.setUser(user);
        }
        
        return transaction;
    }
}
