package com.example.WalletApp.service;

import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.service.impl.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock
    private CurrencyRepository currencyRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    private Currency testCurrency;

    @BeforeEach
    void setUp() {
        testCurrency = new Currency();
        testCurrency.setId(1L);
        testCurrency.setName("USD");
        testCurrency.setValueToEur(1.0);
    }

    @Test
    void testCreateCurrency_Success() {
        // Given
        when(currencyRepository.existsByName("USD")).thenReturn(false);
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);

        // When
        Currency result = currencyService.createCurrency(testCurrency);

        // Then
        assertNotNull(result);
        assertEquals("USD", result.getName());
        assertEquals(1.0, result.getValueToEur());
        verify(currencyRepository).save(testCurrency);
    }

    @Test
    void testCreateCurrency_NameExists() {
        // Given
        when(currencyRepository.existsByName("USD")).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyService.createCurrency(testCurrency);
        });

        assertEquals("Currency with name USD already exists", exception.getMessage());
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void testUpdateCurrency_Success() {
        // Given
        Currency updatedCurrency = new Currency();
        updatedCurrency.setName("EUR");
        updatedCurrency.setValueToEur(1.1);

        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));
        when(currencyRepository.save(any(Currency.class))).thenReturn(testCurrency);

        // When
        Currency result = currencyService.updateCurrency(1L, updatedCurrency);

        // Then
        assertNotNull(result);
        verify(currencyRepository).save(testCurrency);
    }

    @Test
    void testUpdateCurrency_CurrencyNotFound() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyService.updateCurrency(1L, testCurrency);
        });

        assertEquals("Currency not found with id: 1", exception.getMessage());
        verify(currencyRepository, never()).save(any(Currency.class));
    }

    @Test
    void testDeleteCurrency_Success() {
        // Given
        when(currencyRepository.existsById(1L)).thenReturn(true);

        // When
        currencyService.deleteCurrency(1L);

        // Then
        verify(currencyRepository).deleteById(1L);
    }

    @Test
    void testDeleteCurrency_CurrencyNotFound() {
        // Given
        when(currencyRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            currencyService.deleteCurrency(1L);
        });

        assertEquals("Currency not found with id: 1", exception.getMessage());
        verify(currencyRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCurrencyById_Found() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.of(testCurrency));

        // When
        Optional<Currency> result = currencyService.getCurrencyById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCurrency, result.get());
    }

    @Test
    void testGetCurrencyById_NotFound() {
        // Given
        when(currencyRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Currency> result = currencyService.getCurrencyById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllCurrencies() {
        // Given
        List<Currency> currencies = Arrays.asList(testCurrency);
        when(currencyRepository.findAll()).thenReturn(currencies);

        // When
        List<Currency> result = currencyService.getAllCurrencies();

        // Then
        assertEquals(1, result.size());
        assertEquals(testCurrency, result.get(0));
    }

    @Test
    void testGetCurrencyByName_Found() {
        // Given
        when(currencyRepository.findByName("USD")).thenReturn(Optional.of(testCurrency));

        // When
        Optional<Currency> result = currencyService.getCurrencyByName("USD");

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCurrency, result.get());
    }

    @Test
    void testGetCurrencyByName_NotFound() {
        // Given
        when(currencyRepository.findByName("USD")).thenReturn(Optional.empty());

        // When
        Optional<Currency> result = currencyService.getCurrencyByName("USD");

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testExistsByName_True() {
        // Given
        when(currencyRepository.existsByName("USD")).thenReturn(true);

        // When
        boolean result = currencyService.existsByName("USD");

        // Then
        assertTrue(result);
    }

    @Test
    void testExistsByName_False() {
        // Given
        when(currencyRepository.existsByName("USD")).thenReturn(false);

        // When
        boolean result = currencyService.existsByName("USD");

        // Then
        assertFalse(result);
    }
}
