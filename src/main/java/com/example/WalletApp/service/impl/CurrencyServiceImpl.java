package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CurrencyServiceImpl implements CurrencyService {
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Override
    public Currency createCurrency(Currency currency) {
        if (currencyRepository.existsByName(currency.getName())) {
            throw new RuntimeException("Currency with name " + currency.getName() + " already exists");
        }
        return currencyRepository.save(currency);
    }
    
    @Override
    public Currency updateCurrency(Long id, Currency currency) {
        Currency existingCurrency = currencyRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Currency not found with id: " + id));
        
        existingCurrency.setName(currency.getName());
        existingCurrency.setValueToEur(currency.getValueToEur());
        
        return currencyRepository.save(existingCurrency);
    }
    
    @Override
    public void deleteCurrency(Long id) {
        if (!currencyRepository.existsById(id)) {
            throw new RuntimeException("Currency not found with id: " + id);
        }
        currencyRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Currency> getCurrencyById(Long id) {
        return currencyRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Currency> getAllCurrencies(Pageable pageable) {
        return currencyRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Currency> getCurrencyByName(String name) {
        return currencyRepository.findByName(name);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return currencyRepository.existsByName(name);
    }
    
    @Override
    public CurrencyDTO convertToDTO(Currency currency) {
        CurrencyDTO dto = new CurrencyDTO();
        dto.setId(currency.getId());
        dto.setName(currency.getName());
        dto.setValueToEur(currency.getValueToEur());
        return dto;
    }
    
    @Override
    public Currency convertToEntity(CurrencyDTO dto) {
        Currency currency = new Currency();
        currency.setId(dto.getId());
        currency.setName(dto.getName());
        currency.setValueToEur(dto.getValueToEur());
        return currency;
    }
}
