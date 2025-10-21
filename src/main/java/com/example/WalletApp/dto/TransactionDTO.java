package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class TransactionDTO {
    
    private Long id;
    
    @NotBlank(message = "Naziv transakcije je obavezan")
    private String name;
    
    @NotNull(message = "Iznos je obavezan")
    private BigDecimal amount;
    
    @NotBlank(message = "Tip transakcije je obavezan")
    private String type; // INCOME or EXPENSE
    
    @NotNull(message = "Kategorija je obavezna")
    private Long categoryId;
    
    private String categoryName;
    
    @NotNull(message = "Novčanik je obavezan")
    private Long walletId;
    
    private String walletName;
    private Date dateOfTransaction;
    private Long userId;
    private String userName;
    private boolean repeating;
    private String frequency; // WEEKLY, MONTHLY, QUARTERLY, YEARLY
    
    // Constructors
    public TransactionDTO() {}
    
    public TransactionDTO(Long id, String name, BigDecimal amount, String type,
                         Long categoryId, String categoryName, Long walletId, String walletName,
                         Date dateOfTransaction, Long userId, String userName,
                         boolean repeating, String frequency) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.walletId = walletId;
        this.walletName = walletName;
        this.dateOfTransaction = dateOfTransaction;
        this.userId = userId;
        this.userName = userName;
        this.repeating = repeating;
        this.frequency = frequency;
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
    
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public String getWalletName() { return walletName; }
    public void setWalletName(String walletName) { this.walletName = walletName; }
    
    public Date getDateOfTransaction() { return dateOfTransaction; }
    public void setDateOfTransaction(Date dateOfTransaction) { this.dateOfTransaction = dateOfTransaction; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    
    public boolean isRepeating() { return repeating; }
    public void setRepeating(boolean repeating) { this.repeating = repeating; }
    
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }
}
