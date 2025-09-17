package com.example.WalletApp.service;

import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.entity.Currency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    
    // CRUD Operations
    Currency createCurrency(Currency currency);
    Currency updateCurrency(Long id, Currency currency);
    void deleteCurrency(Long id);
    Optional<Currency> getCurrencyById(Long id);
    List<Currency> getAllCurrencies();
    Page<Currency> getAllCurrencies(Pageable pageable);
    
    // Currency operations
    Optional<Currency> getCurrencyByName(String name);
    boolean existsByName(String name);
    
    // DTO Conversion
    CurrencyDTO convertToDTO(Currency currency);
    Currency convertToEntity(CurrencyDTO currencyDTO);
}
