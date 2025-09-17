package com.example.WalletApp.service;

import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.entity.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface WalletService {
    
    // CRUD Operations
    Wallet createWallet(Wallet wallet);
    Wallet updateWallet(Long id, Wallet wallet);
    void deleteWallet(Long id);
    Optional<Wallet> getWalletById(Long id);
    List<Wallet> getAllWallets();
    Page<Wallet> getAllWallets(Pageable pageable);
    
    // User-specific operations
    List<Wallet> getWalletsByUserId(Long userId);
    List<Wallet> getActiveWalletsByUserId(Long userId);
    List<Wallet> getSavingsWalletsByUserId(Long userId);
    BigDecimal getTotalBalanceByUserId(Long userId);
    
    // Wallet management
    void archiveWallet(Long walletId);
    void unarchiveWallet(Long walletId);
    void addToWalletBalance(Long walletId, BigDecimal amount);
    void subtractFromWalletBalance(Long walletId, BigDecimal amount);
    
    // Statistics
    BigDecimal getAverageWalletBalance();
    BigDecimal getTotalSystemBalance();
    List<Wallet> getWalletsByCurrency(Long currencyId);
    
    // DTO Conversion
    WalletDTO convertToDTO(Wallet wallet);
    Wallet convertToEntity(WalletDTO walletDTO);
}
