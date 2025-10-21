package com.example.WalletApp.controller;

import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Wallet CRUD operations.
 * Uses Path Variables for resource identification.
 */
@RestController
@RequestMapping("/wallets")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    /**
     * Create a new wallet.
     * POST /api/wallets
     */
    @PostMapping
    public ResponseEntity<?> createWallet(@Valid @RequestBody WalletDTO walletDTO, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            WalletDTO createdWallet = walletService.createWallet(walletDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(createdWallet);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all wallets for current user.
     * GET /api/wallets
     */
    @GetMapping
    public ResponseEntity<?> getUserWallets(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<WalletDTO> wallets = walletService.getUserWallets(user.getId());
            return ResponseEntity.ok(wallets);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get active wallets for current user.
     * GET /api/wallets/active
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveWallets(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<WalletDTO> wallets = walletService.getActiveUserWallets(user.getId());
            return ResponseEntity.ok(wallets);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get wallet by ID (Path Variable).
     * GET /api/wallets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getWalletById(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            WalletDTO wallet = walletService.getWalletById(id, user.getId());
            return ResponseEntity.ok(wallet);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update wallet.
     * PUT /api/wallets/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateWallet(@PathVariable Long id, 
                                         @Valid @RequestBody WalletDTO walletDTO, 
                                         HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            WalletDTO updatedWallet = walletService.updateWallet(id, walletDTO, user.getId());
            return ResponseEntity.ok(updatedWallet);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Archive wallet.
     * PUT /api/wallets/{id}/archive
     */
    @PutMapping("/{id}/archive")
    public ResponseEntity<?> archiveWallet(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            WalletDTO wallet = walletService.archiveWallet(id, user.getId());
            return ResponseEntity.ok(wallet);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Unarchive wallet.
     * PUT /api/wallets/{id}/unarchive
     */
    @PutMapping("/{id}/unarchive")
    public ResponseEntity<?> unarchiveWallet(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            WalletDTO wallet = walletService.unarchiveWallet(id, user.getId());
            return ResponseEntity.ok(wallet);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete wallet.
     * DELETE /api/wallets/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWallet(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            walletService.deleteWallet(id, user.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
