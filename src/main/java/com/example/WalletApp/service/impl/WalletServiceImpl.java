package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import com.example.WalletApp.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementacija servisa za upravljanje novčanicima
 * 
 * Ova klasa sadrži poslovne logike vezane za novčanike:
 * - Kreiranje i ažuriranje novčanika
 * - Upravljanje balansom novčanika
 * - Pretragu novčanika po korisniku
 * - Statistike o novčanicima
 * - Konverziju između entiteta i DTO objekata
 * 
 * @author vuksta
 * @version 1.0
 */
@Service
@Transactional
public class WalletServiceImpl implements WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    /**
     * Kreira novi novčanik
     * 
     * @param wallet - novčanik koji se kreira
     * @return sačuvan novčanik sa generisanim ID-jem
     */
    @Override
    public Wallet createWallet(Wallet wallet) {
        return walletRepository.save(wallet);
    }
    
    /**
     * Ažurira postojeći novčanik
     * 
     * Ne ažurira početni balans, trenutni balans ili datum kreiranja
     * jer su to nepromenljivi podaci.
     * 
     * @param id - ID novčanika koji se ažurira
     * @param wallet - novi podaci novčanika
     * @return ažuriran novčanik
     * @throws RuntimeException ako novčanik ne postoji
     */
    @Override
    public Wallet updateWallet(Long id, Wallet wallet) {
        Wallet existingWallet = walletRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + id));
        
        // Ažurira samo dozvoljene podatke
        existingWallet.setName(wallet.getName());
        existingWallet.setSavings(wallet.isSavings());
        
        // Ažurira valutu samo ako je prosleđena
        if (wallet.getCurrency() != null) {
            existingWallet.setCurrency(wallet.getCurrency());
        }
        
        return walletRepository.save(existingWallet);
    }
    
    @Override
    public void deleteWallet(Long id) {
        if (!walletRepository.existsById(id)) {
            throw new RuntimeException("Wallet not found with id: " + id);
        }
        walletRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Wallet> getWalletById(Long id) {
        return walletRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getAllWallets() {
        return walletRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Wallet> getAllWallets(Pageable pageable) {
        return walletRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByUserId(Long userId) {
        return walletRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getActiveWalletsByUserId(Long userId) {
        return walletRepository.findActiveWalletsByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getSavingsWalletsByUserId(Long userId) {
        return walletRepository.findByUserIdAndSavings(userId, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalBalanceByUserId(Long userId) {
        BigDecimal total = walletRepository.getTotalBalanceByUserId(userId);
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    public void archiveWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
        wallet.archive();
        walletRepository.save(wallet);
    }
    
    @Override
    public void unarchiveWallet(Long walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
        wallet.unarchive();
        walletRepository.save(wallet);
    }
    
    @Override
    public void addToWalletBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
        wallet.updateBalance(amount);
        walletRepository.save(wallet);
    }
    
    @Override
    public void subtractFromWalletBalance(Long walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new RuntimeException("Wallet not found with id: " + walletId));
        
        if (wallet.getCurrentBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance in wallet: " + walletId);
        }
        
        wallet.updateBalance(amount.negate());
        walletRepository.save(wallet);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAverageWalletBalance() {
        BigDecimal average = walletRepository.getAverageWalletBalance();
        return average != null ? average : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSystemBalance() {
        BigDecimal total = walletRepository.getTotalSystemBalance();
        return total != null ? total : BigDecimal.ZERO;
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Wallet> getWalletsByCurrency(Long currencyId) {
        return walletRepository.findByCurrencyId(currencyId);
    }
    
    /**
     * Konvertuje Wallet entitet u WalletDTO
     * 
     * @param wallet - novčanik entitet
     * @return WalletDTO objekat
     */
    @Override
    public WalletDTO convertToDTO(Wallet wallet) {
        WalletDTO dto = new WalletDTO();
        dto.setId(wallet.getId());
        dto.setName(wallet.getName());
        dto.setInitialBalance(wallet.getInitialBalance());
        dto.setCurrentBalance(wallet.getCurrentBalance());
        dto.setDateOfCreation(wallet.getDateOfCreation());
        dto.setSavings(wallet.isSavings());
        dto.setArchived(wallet.isArchived());
        
        // Postavlja ID korisnika ako novčanik ima vlasnika
        if (wallet.getUser() != null) {
            dto.setUserId(wallet.getUser().getId());
        }
        
        // Postavlja ID valute ako novčanik ima valutu
        if (wallet.getCurrency() != null) {
            dto.setCurrencyId(wallet.getCurrency().getId());
        }
        
        return dto;
    }
    
    /**
     * Konvertuje WalletDTO u Wallet entitet
     * 
     * Ova metoda postavlja default vrednosti za polja koja nisu obavezna:
     * - Ako trenutni balans nije prosleđen, postavlja se na početni balans
     * - Ako datum kreiranja nije prosleđen, postavlja se na trenutni datum
     * 
     * @param dto - DTO objekat sa podacima novčanika
     * @return Wallet entitet
     */
    @Override
    public Wallet convertToEntity(WalletDTO dto) {
        Wallet wallet = new Wallet();
        wallet.setId(dto.getId());
        wallet.setName(dto.getName());
        wallet.setInitialBalance(dto.getInitialBalance());
        
        // Postavlja trenutni balans na početni ako nije prosleđen
        if (dto.getCurrentBalance() != null) {
            wallet.setCurrentBalance(dto.getCurrentBalance());
        } else {
            wallet.setCurrentBalance(dto.getInitialBalance());
        }
        
        // Postavlja datum kreiranja na trenutni datum ako nije prosleđen
        if (dto.getDateOfCreation() != null) {
            wallet.setDateOfCreation(dto.getDateOfCreation());
        } else {
            wallet.setDateOfCreation(new Date());
        }
        
        wallet.setSavings(dto.isSavings());
        wallet.setArchived(dto.isArchived());
        
        // Postavlja korisnika ako je prosleđen ID korisnika
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            wallet.setUser(user);
        }
        
        // Postavlja valutu ako je prosleđen ID valute
        if (dto.getCurrencyId() != null) {
            Currency currency = currencyRepository.findById(dto.getCurrencyId()).orElse(null);
            wallet.setCurrency(currency);
        }
        
        return wallet;
    }
}
