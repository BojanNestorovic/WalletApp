package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/currencies")
@CrossOrigin(origins = "*")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    // Get all currencies
    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getAllCurrencies() {
        List<Currency> currencies = currencyService.getAllCurrencies();
        List<CurrencyDTO> currencyDTOs = currencies.stream()
            .map(currencyService::convertToDTO)
            .toList();
        return ResponseEntity.ok(currencyDTOs);
    }

    // Get all currencies with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<CurrencyDTO>> getAllCurrencies(Pageable pageable) {
        Page<Currency> currencies = currencyService.getAllCurrencies(pageable);
        Page<CurrencyDTO> currencyDTOs = currencies.map(currencyService::convertToDTO);
        return ResponseEntity.ok(currencyDTOs);
    }

    // Get currency by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCurrencyById(@PathVariable Long id) {
        Optional<Currency> currency = currencyService.getCurrencyById(id);
        if (currency.isPresent()) {
            CurrencyDTO currencyDTO = currencyService.convertToDTO(currency.get());
            return ResponseEntity.ok(currencyDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Get currency by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getCurrencyByName(@PathVariable String name) {
        Optional<Currency> currency = currencyService.getCurrencyByName(name);
        if (currency.isPresent()) {
            CurrencyDTO currencyDTO = currencyService.convertToDTO(currency.get());
            return ResponseEntity.ok(currencyDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new currency
    @PostMapping
    public ResponseEntity<?> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO) {
        try {
            Currency currency = currencyService.convertToEntity(currencyDTO);
            Currency createdCurrency = currencyService.createCurrency(currency);
            CurrencyDTO responseDTO = currencyService.convertToDTO(createdCurrency);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating currency: " + e.getMessage());
        }
    }

    // Update currency
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrency(@PathVariable Long id, @Valid @RequestBody CurrencyDTO currencyDTO) {
        try {
            Currency currency = currencyService.convertToEntity(currencyDTO);
            Currency updatedCurrency = currencyService.updateCurrency(id, currency);
            CurrencyDTO responseDTO = currencyService.convertToDTO(updatedCurrency);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating currency: " + e.getMessage());
        }
    }

    // Delete currency
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id) {
        try {
            currencyService.deleteCurrency(id);
            return ResponseEntity.ok().body("Currency deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting currency: " + e.getMessage());
        }
    }

    // Check if currency exists by name
    @GetMapping("/exists/{name}")
    public ResponseEntity<?> checkCurrencyExists(@PathVariable String name) {
        boolean exists = currencyService.existsByName(name);
        return ResponseEntity.ok().body("Currency exists: " + exists);
    }
}
