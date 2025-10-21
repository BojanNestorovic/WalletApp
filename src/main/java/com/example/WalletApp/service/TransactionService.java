package com.example.WalletApp.service;

import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.dto.TransferDTO;
import com.example.WalletApp.entity.*;
import com.example.WalletApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Transaction management with atomic transfer operations.
 * Critical: Uses @Transactional for wallet-to-wallet transfers with currency conversion.
 */
@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Create a new transaction and update wallet balance atomically.
     */
    @Transactional
    public TransactionDTO createTransaction(TransactionDTO transactionDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        Wallet wallet = walletRepository.findById(transactionDTO.getWalletId())
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        Category category = categoryRepository.findById(transactionDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Kategorija ne postoji"));
        
        Transaction transaction = new Transaction();
        transaction.setName(transactionDTO.getName());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setType(TransactionType.valueOf(transactionDTO.getType()));
        transaction.setCategory(category);
        transaction.setWallet(wallet);
        transaction.setUser(user);
        transaction.setRepeating(transactionDTO.isRepeating());
        
        if (transactionDTO.isRepeating() && transactionDTO.getFrequency() != null) {
            transaction.setFrequency(Frequency.valueOf(transactionDTO.getFrequency()));
        }
        
        if (transactionDTO.getDateOfTransaction() != null) {
            transaction.setDateOfTransaction(transactionDTO.getDateOfTransaction());
        }
        
        // Update wallet balance atomically
        if (transaction.isIncome()) {
            wallet.updateBalance(transaction.getAmount());
        } else {
            wallet.updateBalance(transaction.getAmount().negate());
        }
        
        walletRepository.save(wallet);
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        return convertToDTO(savedTransaction);
    }
    
    /**
     * CRITICAL: Transfer funds between wallets with currency conversion.
     * This method MUST be atomic - all steps succeed or all fail.
     */
    @Transactional
    public void transferFunds(TransferDTO transferDTO, Long userId) {
        // Step 1: Validate wallets
        Wallet fromWallet = walletRepository.findById(transferDTO.getFromWalletId())
                .orElseThrow(() -> new RuntimeException("Izvorni novčanik ne postoji"));
        
        Wallet toWallet = walletRepository.findById(transferDTO.getToWalletId())
                .orElseThrow(() -> new RuntimeException("Odredišni novčanik ne postoji"));
        
        if (!fromWallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup izvornom novčaniku");
        }
        
        if (!toWallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup odredišnom novčaniku");
        }
        
        // Step 2: Validate sufficient funds
        if (fromWallet.getCurrentBalance().compareTo(transferDTO.getAmount()) < 0) {
            throw new RuntimeException("Nedovoljno sredstava na izvornom novčaniku");
        }
        
        // Step 3: Calculate conversion if currencies differ
        BigDecimal transferAmount = transferDTO.getAmount();
        BigDecimal receivedAmount = transferAmount;
        
        if (!fromWallet.getCurrency().getId().equals(toWallet.getCurrency().getId())) {
            // Convert through EUR as base currency
            Double fromRate = fromWallet.getCurrency().getValueToEur();
            Double toRate = toWallet.getCurrency().getValueToEur();
            
            // Convert to EUR, then to target currency
            BigDecimal amountInEur = transferAmount.multiply(BigDecimal.valueOf(fromRate));
            receivedAmount = amountInEur.divide(BigDecimal.valueOf(toRate), 2, RoundingMode.HALF_UP);
        }
        
        // Step 4: Update balances atomically
        fromWallet.updateBalance(transferAmount.negate());
        toWallet.updateBalance(receivedAmount);
        
        // Step 5: Create transaction records
        User user = userRepository.findById(userId).orElseThrow();
        Category transferCategory = categoryRepository.findById(1L).orElseThrow(); // Default category
        
        // Expense transaction from source wallet
        Transaction expenseTransaction = new Transaction();
        expenseTransaction.setName("Transfer: " + (transferDTO.getDescription() != null ? transferDTO.getDescription() : "Prenos sredstava"));
        expenseTransaction.setAmount(transferAmount);
        expenseTransaction.setType(TransactionType.EXPENSE);
        expenseTransaction.setCategory(transferCategory);
        expenseTransaction.setWallet(fromWallet);
        expenseTransaction.setUser(user);
        
        // Income transaction to destination wallet
        Transaction incomeTransaction = new Transaction();
        incomeTransaction.setName("Transfer: " + (transferDTO.getDescription() != null ? transferDTO.getDescription() : "Prenos sredstava"));
        incomeTransaction.setAmount(receivedAmount);
        incomeTransaction.setType(TransactionType.INCOME);
        incomeTransaction.setCategory(transferCategory);
        incomeTransaction.setWallet(toWallet);
        incomeTransaction.setUser(user);
        
        // Step 6: Save all changes (atomic commit)
        walletRepository.save(fromWallet);
        walletRepository.save(toWallet);
        transactionRepository.save(expenseTransaction);
        transactionRepository.save(incomeTransaction);
    }
    
    /**
     * Get all transactions for a user.
     */
    public List<TransactionDTO> getUserTransactions(Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get transactions for a specific wallet.
     */
    public List<TransactionDTO> getWalletTransactions(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        return transactionRepository.findByWalletId(walletId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get transactions in date range.
     */
    public List<TransactionDTO> getTransactionsByDateRange(Long userId, Date startDate, Date endDate) {
        return transactionRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get Top 10 transactions by amount for a specific period.
     */
    public List<TransactionDTO> getTopTransactions(Date fromDate, int limit) {
        return transactionRepository.findTop10TransactionsByDate(fromDate).stream()
                .limit(limit)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all transactions (admin only).
     */
    public List<TransactionDTO> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete transaction and revert wallet balance.
     * Only admin can delete transactions with predefined categories.
     * Regular users can only delete transactions with custom categories.
     */
    @Transactional
    public void deleteTransaction(Long transactionId, Long userId, String userRole) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new RuntimeException("Transakcija ne postoji"));
        
        if (!transaction.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovoj transakciji");
        }
        
        // Check if category is predefined and user is not admin
        Category category = transaction.getCategory();
        if (category.isPredefined() && !"ADMINISTRATOR".equals(userRole)) {
            throw new RuntimeException("Neautorizovano brisanje. Za brisanje transakcija sa predefinisanim kategorijama, kontaktirajte administratora.");
        }
        
        // Revert wallet balance
        Wallet wallet = transaction.getWallet();
        if (transaction.isIncome()) {
            wallet.updateBalance(transaction.getAmount().negate());
        } else {
            wallet.updateBalance(transaction.getAmount());
        }
        
        walletRepository.save(wallet);
        transactionRepository.delete(transaction);
    }
    
    /**
     * Convert Transaction entity to TransactionDTO.
     */
    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
            transaction.getId(),
            transaction.getName(),
            transaction.getAmount(),
            transaction.getType().toString(),
            transaction.getCategory().getId(),
            transaction.getCategory().getName(),
            transaction.getWallet().getId(),
            transaction.getWallet().getName(),
            transaction.getDateOfTransaction(),
            transaction.getUser().getId(),
            transaction.getUser().getUsername(),
            transaction.isRepeating(),
            transaction.getFrequency() != null ? transaction.getFrequency().toString() : null
        );
    }
}
