package com.example.WalletApp.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionDTO {
    private Long id;
    
    @NotBlank(message = "Transaction name is required")
    @Size(min = 2, max = 100, message = "Transaction name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    private BigDecimal amount;
    
    @NotBlank(message = "Transaction type is required")
    private String type;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    private Date dateOfTransaction;
    private boolean repeating;
    private String frequency;
    private Long walletId;
    private Long userId;
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(String name, BigDecimal amount, String type, Long categoryId, 
                         Long walletId, Long userId) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.walletId = walletId;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Date getDateOfTransaction() { return dateOfTransaction; }
    public void setDateOfTransaction(Date dateOfTransaction) { this.dateOfTransaction = dateOfTransaction; }
    
    public boolean isRepeating() { return repeating; }
    public void setRepeating(boolean repeating) { this.repeating = repeating; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
    
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
