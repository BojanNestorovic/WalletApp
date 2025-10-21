package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class WalletDTO {
    
    private Long id;
    
    @NotBlank(message = "Naziv novčanika je obavezan")
    private String name;
    
    @NotNull(message = "Početno stanje je obavezno")
    private BigDecimal initialBalance;
    
    private BigDecimal currentBalance;
    
    @NotNull(message = "Valuta je obavezna")
    private Long currencyId;
    
    private String currencyName;
    private Date dateOfCreation;
    private Long userId;
    private boolean savings;
    private boolean archived;
    
    // Constructors
    public WalletDTO() {}
    
    public WalletDTO(Long id, String name, BigDecimal initialBalance, BigDecimal currentBalance,
                     Long currencyId, String currencyName, Date dateOfCreation, Long userId,
                     boolean savings, boolean archived) {
        this.id = id;
        this.name = name;
        this.initialBalance = initialBalance;
        this.currentBalance = currentBalance;
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.dateOfCreation = dateOfCreation;
        this.userId = userId;
        this.savings = savings;
        this.archived = archived;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getInitialBalance() { return initialBalance; }
    public void setInitialBalance(BigDecimal initialBalance) { this.initialBalance = initialBalance; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public Long getCurrencyId() { return currencyId; }
    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }
    
    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
    
    public Date getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(Date dateOfCreation) { this.dateOfCreation = dateOfCreation; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public boolean isSavings() { return savings; }
    public void setSavings(boolean savings) { this.savings = savings; }
    
    public boolean isArchived() { return archived; }
    public void setArchived(boolean archived) { this.archived = archived; }
}
