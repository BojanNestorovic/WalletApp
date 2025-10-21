package com.example.WalletApp.service;

import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for Currency management with external API integration.
 */
@Service
public class CurrencyService {
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    /**
     * Get all currencies.
     */
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get currency by ID.
     */
    public CurrencyDTO getCurrencyById(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
        return convertToDTO(currency);
    }
    
    /**
     * Create new currency (admin only).
     */
    @Transactional
    public CurrencyDTO createCurrency(CurrencyDTO currencyDTO) {
        Currency currency = new Currency();
        currency.setName(currencyDTO.getName());
        currency.setValueToEur(currencyDTO.getValueToEur());
        
        Currency savedCurrency = currencyRepository.save(currency);
        return convertToDTO(savedCurrency);
    }
    
    /**
     * Update currency exchange rate (admin only).
     */
    @Transactional
    public CurrencyDTO updateCurrency(Long id, CurrencyDTO currencyDTO) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
        
        currency.setName(currencyDTO.getName());
        currency.setValueToEur(currencyDTO.getValueToEur());
        
        Currency updatedCurrency = currencyRepository.save(currency);
        return convertToDTO(updatedCurrency);
    }
    
    /**
     * BONUS: Fetch latest exchange rate from Frankfurter API.
     * Example: https://api.frankfurter.app/latest?from=EUR
     */
    public Double fetchExchangeRateFromAPI(String currencyCode) {
        try {
            String apiUrl = "https://api.frankfurter.app/latest?from=EUR&to=" + currencyCode;
            RestTemplate restTemplate = new RestTemplate();
            
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(apiUrl, Map.class);
            
            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Double> rates = (Map<String, Double>) response.get("rates");
                
                if (rates.containsKey(currencyCode)) {
                    Double rate = rates.get(currencyCode);
                    // Convert to valueToEur (inverse of rate from EUR)
                    return 1.0 / rate;
                }
            }
            
            throw new RuntimeException("Nije moguće preuzeti kurs za valutu: " + currencyCode);
            
        } catch (Exception e) {
            throw new RuntimeException("Greška pri preuzimanju kursa: " + e.getMessage());
        }
    }
    
    /**
     * Update currency using external API (admin only).
     */
    @Transactional
    public CurrencyDTO updateCurrencyFromAPI(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
        
        if (currency.getName().equals("EUR")) {
            throw new RuntimeException("EUR je osnovna valuta i ne može se ažurirati");
        }
        
        Double newRate = fetchExchangeRateFromAPI(currency.getName());
        currency.setValueToEur(newRate);
        
        Currency updatedCurrency = currencyRepository.save(currency);
        return convertToDTO(updatedCurrency);
    }
    
    /**
     * Delete currency (admin only).
     */
    @Transactional
    public void deleteCurrency(Long id) {
        Currency currency = currencyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
        
        currencyRepository.delete(currency);
    }
    
    /**
     * Convert Currency entity to CurrencyDTO.
     */
    private CurrencyDTO convertToDTO(Currency currency) {
        return new CurrencyDTO(
            currency.getId(),
            currency.getName(),
            currency.getValueToEur()
        );
    }
}
