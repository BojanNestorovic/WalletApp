package com.example.WalletApp.controller;

import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.entity.Transaction;
import com.example.WalletApp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Get all transactions
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get all transactions with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<TransactionDTO>> getAllTransactions(Pageable pageable) {
        Page<Transaction> transactions = transactionService.getAllTransactions(pageable);
        Page<TransactionDTO> transactionDTOs = transactions.map(transactionService::convertToDTO);
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transaction by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        if (transaction.isPresent()) {
            TransactionDTO transactionDTO = transactionService.convertToDTO(transaction.get());
            return ResponseEntity.ok(transactionDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new transaction
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            Transaction transaction = transactionService.convertToEntity(transactionDTO);
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            TransactionDTO responseDTO = transactionService.convertToDTO(createdTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating transaction: " + e.getMessage());
        }
    }

    // Update transaction
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@PathVariable Long id, @Valid @RequestBody TransactionDTO transactionDTO) {
        try {
            Transaction transaction = transactionService.convertToEntity(transactionDTO);
            Transaction updatedTransaction = transactionService.updateTransaction(id, transaction);
            TransactionDTO responseDTO = transactionService.convertToDTO(updatedTransaction);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating transaction: " + e.getMessage());
        }
    }

    // Delete transaction
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id) {
        try {
            transactionService.deleteTransaction(id);
            return ResponseEntity.ok().body("Transaction deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting transaction: " + e.getMessage());
        }
    }

    // Get transactions by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserId(@PathVariable Long userId) {
        List<Transaction> transactions = transactionService.getTransactionsByUserId(userId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transactions by wallet ID
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByWalletId(@PathVariable Long walletId) {
        List<Transaction> transactions = transactionService.getTransactionsByWalletId(walletId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transactions by category ID
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByCategoryId(@PathVariable Long categoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByCategoryId(categoryId);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transactions by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(@PathVariable String type) {
        List<Transaction> transactions = transactionService.getTransactionsByType(type);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get repeating transactions
    @GetMapping("/repeating")
    public ResponseEntity<List<TransactionDTO>> getRepeatingTransactions() {
        List<Transaction> transactions = transactionService.getRepeatingTransactions();
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transactions by date range
    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transactions by user and date range
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByUserAndDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByUserAndDateRange(userId, startDate, endDate);
        List<TransactionDTO> transactionDTOs = transactions.stream()
            .map(transactionService::convertToDTO)
            .toList();
        return ResponseEntity.ok(transactionDTOs);
    }

    // Get transaction statistics for user
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getTransactionStatsByUserId(@PathVariable Long userId) {
        double totalIncome = transactionService.getTotalIncomeByUserId(userId);
        double totalExpense = transactionService.getTotalExpenseByUserId(userId);
        double netBalance = transactionService.getNetBalanceByUserId(userId);
        
        return ResponseEntity.ok().body(String.format(
            "Total Income: %.2f, Total Expense: %.2f, Net Balance: %.2f", 
            totalIncome, totalExpense, netBalance));
    }
}
