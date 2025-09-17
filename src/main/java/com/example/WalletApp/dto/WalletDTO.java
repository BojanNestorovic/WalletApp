package com.example.WalletApp.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object (DTO) za novčanik
 * 
 * DTO sadrži podatke o novčaniku koji se koriste za prenos između slojeva.
 * Uključuje validacione anotacije za osnovne podatke novčanika.
 * 
 * @author vuksta
 * @version 1.0
 */
public class WalletDTO {
    private Long id;
    
    // Naziv novčanika - obavezno polje
    @NotBlank(message = "Wallet name is required")
    @Size(min = 2, max = 50, message = "Wallet name must be between 2 and 50 characters")
    private String name;
    
    // Početni balans - mora biti nenegativan
    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Initial balance must be non-negative")
    private BigDecimal initialBalance;
    
    // Trenutni balans - može se razlikovati od početnog
    private BigDecimal currentBalance;
    
    // Datum kreiranja novčanika
    private Date dateOfCreation;
    
    // ID korisnika koji poseduje novčanik
    private Long userId;
    
    // ID valute u kojoj je novčanik
    private Long currencyId;
    
    // Da li je novčanik namenjen za štednju
    private boolean savings;
    
    // Da li je novčanik arhiviran
    private boolean archived;
    
    /**
     * Podrazumevani konstruktor
     */
    public WalletDTO() {}
    
    /**
     * Konstruktor sa osnovnim podacima novčanika
     * 
     * @param name - naziv novčanika
     * @param initialBalance - početni balans
     * @param userId - ID korisnika
     * @param currencyId - ID valute
     */
    public WalletDTO(String name, BigDecimal initialBalance, Long userId, Long currencyId) {
        this.name = name;
        this.initialBalance = initialBalance;
        this.userId = userId;
        this.currencyId = currencyId;
    }
    
    // Getters and Setters - omogućavaju pristup privatnim poljima
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public Date getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(Date dateOfCreation) { this.dateOfCreation = dateOfCreation; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getCurrencyId() { return currencyId; }
    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }
    
    public boolean isSavings() { return savings; }
    public void setSavings(boolean savings) { this.savings = savings; }
    
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
