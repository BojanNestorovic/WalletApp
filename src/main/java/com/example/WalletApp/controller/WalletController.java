package com.example.WalletApp.controller;

import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*")
public class WalletController {

    @Autowired
    private WalletService walletService;

    // Get all wallets
    @GetMapping
    public ResponseEntity<List<WalletDTO>> getAllWallets() {
        List<Wallet> wallets = walletService.getAllWallets();
        List<WalletDTO> walletDTOs = wallets.stream()
            .map(walletService::convertToDTO)
            .toList();
        return ResponseEntity.ok(walletDTOs);
    }

    // Get all wallets with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<WalletDTO>> getAllWallets(Pageable pageable) {
        Page<Wallet> wallets = walletService.getAllWallets(pageable);
        Page<WalletDTO> walletDTOs = wallets.map(walletService::convertToDTO);
        return ResponseEntity.ok(walletDTOs);
    }

    // Get wallet by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@PathVariable Long id) {
        Optional<Wallet> wallet = walletService.getWalletById(id);
        if (wallet.isPresent()) {
            WalletDTO walletDTO = walletService.convertToDTO(wallet.get());
            return ResponseEntity.ok(walletDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new wallet
    @PostMapping
    public ResponseEntity<?> createWallet(@Valid @RequestBody WalletDTO walletDTO) {
        try {
            Wallet wallet = walletService.convertToEntity(walletDTO);
            Wallet createdWallet = walletService.createWallet(wallet);
            WalletDTO responseDTO = walletService.convertToDTO(createdWallet);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating wallet: " + e.getMessage());
        }
    }

    // Update wallet
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWallet(@PathVariable Long id, @Valid @RequestBody WalletDTO walletDTO) {
        try {
            Wallet wallet = walletService.convertToEntity(walletDTO);
            Wallet updatedWallet = walletService.updateWallet(id, wallet);
            WalletDTO responseDTO = walletService.convertToDTO(updatedWallet);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating wallet: " + e.getMessage());
        }
    }

    // Delete wallet
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWallet(@PathVariable Long id) {
        try {
            walletService.deleteWallet(id);
            return ResponseEntity.ok().body("Wallet deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting wallet: " + e.getMessage());
        }
    }

    // Get wallets by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<WalletDTO>> getWalletsByUserId(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getWalletsByUserId(userId);
        List<WalletDTO> walletDTOs = wallets.stream()
            .map(walletService::convertToDTO)
            .toList();
        return ResponseEntity.ok(walletDTOs);
    }

    // Get active wallets by user ID
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<WalletDTO>> getActiveWalletsByUserId(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getActiveWalletsByUserId(userId);
        List<WalletDTO> walletDTOs = wallets.stream()
            .map(walletService::convertToDTO)
            .toList();
        return ResponseEntity.ok(walletDTOs);
    }

    // Get savings wallets by user ID
    @GetMapping("/user/{userId}/savings")
    public ResponseEntity<List<WalletDTO>> getSavingsWalletsByUserId(@PathVariable Long userId) {
        List<Wallet> wallets = walletService.getSavingsWalletsByUserId(userId);
        List<WalletDTO> walletDTOs = wallets.stream()
            .map(walletService::convertToDTO)
            .toList();
        return ResponseEntity.ok(walletDTOs);
    }

    // Get total balance by user ID
    @GetMapping("/user/{userId}/balance")
    public ResponseEntity<?> getTotalBalanceByUserId(@PathVariable Long userId) {
        BigDecimal totalBalance = walletService.getTotalBalanceByUserId(userId);
        return ResponseEntity.ok().body("Total balance: " + totalBalance);
    }

    // Archive wallet
    @PostMapping("/{id}/archive")
    public ResponseEntity<?> archiveWallet(@PathVariable Long id) {
        try {
            walletService.archiveWallet(id);
            return ResponseEntity.ok().body("Wallet archived successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error archiving wallet: " + e.getMessage());
        }
    }

    // Unarchive wallet
    @PostMapping("/{id}/unarchive")
    public ResponseEntity<?> unarchiveWallet(@PathVariable Long id) {
        try {
            walletService.unarchiveWallet(id);
            return ResponseEntity.ok().body("Wallet unarchived successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unarchiving wallet: " + e.getMessage());
        }
    }

    // Add to wallet balance
    @PostMapping("/{id}/add")
    public ResponseEntity<?> addToWalletBalance(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            walletService.addToWalletBalance(id, amount);
            return ResponseEntity.ok().body("Amount added successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding amount: " + e.getMessage());
        }
    }

    // Subtract from wallet balance
    @PostMapping("/{id}/subtract")
    public ResponseEntity<?> subtractFromWalletBalance(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            walletService.subtractFromWalletBalance(id, amount);
            return ResponseEntity.ok().body("Amount subtracted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error subtracting amount: " + e.getMessage());
        }
    }

    // Get wallet statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getWalletStats() {
        BigDecimal averageBalance = walletService.getAverageWalletBalance();
        BigDecimal totalSystemBalance = walletService.getTotalSystemBalance();
        
        return ResponseEntity.ok().body(String.format(
            "Average Wallet Balance: %s, Total System Balance: %s", 
            averageBalance, totalSystemBalance));
    }

    // Get wallets by currency
    @GetMapping("/currency/{currencyId}")
    public ResponseEntity<List<WalletDTO>> getWalletsByCurrency(@PathVariable Long currencyId) {
        List<Wallet> wallets = walletService.getWalletsByCurrency(currencyId);
        List<WalletDTO> walletDTOs = wallets.stream()
            .map(walletService::convertToDTO)
            .toList();
        return ResponseEntity.ok(walletDTOs);
    }
}
