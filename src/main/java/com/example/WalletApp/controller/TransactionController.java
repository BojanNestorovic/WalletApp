package com.example.WalletApp.controller;

import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.dto.TransferDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Transaction operations including wallet transfers.
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    /**
     * Create a new transaction.
     * POST /api/transactions
     */
    @PostMapping
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionDTO transactionDTO, 
                                               HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            TransactionDTO created = transactionService.createTransaction(transactionDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * CRITICAL: Transfer funds between wallets with currency conversion.
     * POST /api/transactions/transfer
     */
    @PostMapping("/transfer")
    public ResponseEntity<?> transferFunds(@Valid @RequestBody TransferDTO transferDTO, 
                                          HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            transactionService.transferFunds(transferDTO, user.getId());
            
            return ResponseEntity.ok(Map.of("message", "Transfer uspešno izvršen"));
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all transactions for current user.
     * GET /api/transactions
     */
    @GetMapping
    public ResponseEntity<?> getUserTransactions(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<TransactionDTO> transactions = transactionService.getUserTransactions(user.getId());
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get transactions for a specific wallet.
     * GET /api/transactions/wallet/{walletId}
     */
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<?> getWalletTransactions(@PathVariable Long walletId, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<TransactionDTO> transactions = transactionService.getWalletTransactions(walletId, user.getId());
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get transactions in date range (Query Params for filtering).
     * GET /api/transactions/range?startDate=...&endDate=...
     */
    @GetMapping("/range")
    public ResponseEntity<?> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<TransactionDTO> transactions = transactionService.getTransactionsByDateRange(
                    user.getId(), startDate, endDate);
            return ResponseEntity.ok(transactions);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete transaction.
     * DELETE /api/transactions/{id}
     * Admin can delete all transactions.
     * Regular users can only delete transactions with custom categories.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransaction(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            transactionService.deleteTransaction(id, user.getId(), user.getRole());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
