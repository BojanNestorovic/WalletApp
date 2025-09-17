package com.example.WalletApp.dto;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

public class SavingsGoalDTO {
    private Long id;
    
    @NotBlank(message = "Goal name is required")
    @Size(min = 2, max = 100, message = "Goal name must be between 2 and 100 characters")
    private String name;
    
    @NotNull(message = "Target amount is required")
    @DecimalMin(value = "0.01", message = "Target amount must be greater than 0")
    private BigDecimal targetAmount;
    
    private BigDecimal currentAmount;
    private Date targetDate;
    private Date dateOfCreation;
    private Long walletId;
    private Long userId;
    private boolean completed;
    
    // Constructors
    public SavingsGoalDTO() {}
    
    public SavingsGoalDTO(String name, BigDecimal targetAmount, Date targetDate, 
                         Long walletId, Long userId) {
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.walletId = walletId;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
    
    public BigDecimal getCurrentAmount() { return currentAmount; }
    public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
    
    public Date getTargetDate() { return targetDate; }
    public void setTargetDate(Date targetDate) { this.targetDate = targetDate; }
    
    public Date getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(Date dateOfCreation) { this.dateOfCreation = dateOfCreation; }
    
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
}
