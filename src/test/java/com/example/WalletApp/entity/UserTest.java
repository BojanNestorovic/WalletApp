package com.example.WalletApp.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.Date;

class UserTest {

    @Test
    void testUserCreation() {
        // Create a currency
        Currency currency = new Currency("RSD", 0.0085);
        
        // Create a user
        User user = new User();
        user.setFirstName("Marko");
        user.setLastName("Petrović");
        user.setUsername("marko.petrovic");
        user.setEmail("marko@example.com");
        user.setPassword("password123");
        user.setBirthDate(new Date());
        user.setRole(Role.USER);
        user.setCurrency(currency);
        
        // Verify user properties
        assertEquals("Marko", user.getFirstName());
        assertEquals("Petrović", user.getLastName());
        assertEquals("marko.petrovic", user.getUsername());
        assertEquals("marko@example.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
        assertEquals(currency, user.getCurrency());
        assertFalse(user.isBlocked());
        assertNotNull(user.getDateOfRegistration());
    }

    @Test
    void testUserWalletRelationship() {
        User user = new User();
        Currency currency = new Currency("EUR", 1.0);
        
        Wallet wallet = new Wallet("Glavni račun", new BigDecimal("1000.00"), user, currency);
        
        user.addWallet(wallet);
        
        assertEquals(1, user.getWallets().size());
        assertTrue(user.getWallets().contains(wallet));
        assertEquals(user, wallet.getUser());
    }

    @Test
    void testUserTransactionRelationship() {
        User user = new User();
        Currency currency = new Currency("EUR", 1.0);
        Wallet wallet = new Wallet("Glavni račun", new BigDecimal("1000.00"), user, currency);
        Category category = new Category("Hrana", CategoryType.EXPENSE, true);
        
        Transaction transaction = new Transaction("Kupovina hrane", new BigDecimal("50.00"), 
                                                TransactionType.EXPENSE, category, wallet, user);
        
        user.addTransaction(transaction);
        
        assertEquals(1, user.getTransactions().size());
        assertTrue(user.getTransactions().contains(transaction));
        assertEquals(user, transaction.getUser());
    }
}
