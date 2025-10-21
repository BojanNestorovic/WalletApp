package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Currency management.
 */
@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    
    @Autowired
    private CurrencyService currencyService;
    
    /**
     * Get all currencies.
     * GET /api/currencies
     */
    @GetMapping
    public ResponseEntity<?> getAllCurrencies() {
        try {
            List<CurrencyDTO> currencies = currencyService.getAllCurrencies();
            return ResponseEntity.ok(currencies);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get currency by ID.
     * GET /api/currencies/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getCurrencyById(@PathVariable Long id) {
        try {
            CurrencyDTO currency = currencyService.getCurrencyById(id);
            return ResponseEntity.ok(currency);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create new currency (admin only).
     * POST /api/currencies
     */
    @PostMapping
    public ResponseEntity<?> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO, 
                                           HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO created = currencyService.createCurrency(currencyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update currency (admin only).
     * PUT /api/currencies/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCurrency(@PathVariable Long id, 
                                           @Valid @RequestBody CurrencyDTO currencyDTO,
                                           HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO updated = currencyService.updateCurrency(id, currencyDTO);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * BONUS: Update currency from external API (admin only).
     * PUT /api/currencies/{id}/update-from-api
     */
    @PutMapping("/{id}/update-from-api")
    public ResponseEntity<?> updateCurrencyFromAPI(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO updated = currencyService.updateCurrencyFromAPI(id);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete currency (admin only).
     * DELETE /api/currencies/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null || !"ADMINISTRATOR".equals(user.getRole())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            currencyService.deleteCurrency(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
