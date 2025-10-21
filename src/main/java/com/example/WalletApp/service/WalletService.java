package com.example.WalletApp.service;

import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Wallet management with CRUD operations.
 */
@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    /**
     * Create a new wallet for a user.
     */
    @Transactional
    public WalletDTO createWallet(WalletDTO walletDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        Currency currency = currencyRepository.findById(walletDTO.getCurrencyId())
                .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
        
        Wallet wallet = new Wallet();
        wallet.setName(walletDTO.getName());
        wallet.setInitialBalance(walletDTO.getInitialBalance());
        wallet.setCurrentBalance(walletDTO.getInitialBalance());
        wallet.setUser(user);
        wallet.setCurrency(currency);
        wallet.setSavings(walletDTO.isSavings());
        
        Wallet savedWallet = walletRepository.save(wallet);
        return convertToDTO(savedWallet);
    }
    
    /**
     * Get all wallets for a user.
     */
    public List<WalletDTO> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get active (non-archived) wallets for a user.
     */
    public List<WalletDTO> getActiveUserWallets(Long userId) {
        return walletRepository.findByUserIdAndArchived(userId, false).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get wallet by ID with ownership validation.
     */
    public WalletDTO getWalletById(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        return convertToDTO(wallet);
    }
    
    /**
     * Update wallet details.
     */
    @Transactional
    public WalletDTO updateWallet(Long walletId, WalletDTO walletDTO, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        wallet.setName(walletDTO.getName());
        wallet.setSavings(walletDTO.isSavings());
        
        Wallet updatedWallet = walletRepository.save(wallet);
        return convertToDTO(updatedWallet);
    }
    
    /**
     * Archive wallet.
     */
    @Transactional
    public WalletDTO archiveWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        wallet.archive();
        Wallet updatedWallet = walletRepository.save(wallet);
        return convertToDTO(updatedWallet);
    }
    
    /**
     * Unarchive wallet.
     */
    @Transactional
    public WalletDTO unarchiveWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        wallet.unarchive();
        Wallet updatedWallet = walletRepository.save(wallet);
        return convertToDTO(updatedWallet);
    }
    
    /**
     * Delete wallet.
     */
    @Transactional
    public void deleteWallet(Long walletId, Long userId) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        walletRepository.delete(wallet);
    }
    
    /**
     * Get total balance for a user.
     */
    public BigDecimal getTotalUserBalance(Long userId) {
        BigDecimal total = walletRepository.getTotalBalanceByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    /**
     * Convert Wallet entity to WalletDTO.
     */
    private WalletDTO convertToDTO(Wallet wallet) {
        return new WalletDTO(
            wallet.getId(),
            wallet.getName(),
            wallet.getInitialBalance(),
            wallet.getCurrentBalance(),
            wallet.getCurrency().getId(),
            wallet.getCurrency().getName(),
            wallet.getDateOfCreation(),
            wallet.getUser().getId(),
            wallet.isSavings(),
            wallet.isArchived()
        );
    }
}
