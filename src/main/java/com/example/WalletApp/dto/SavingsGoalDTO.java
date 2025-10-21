package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;

public class SavingsGoalDTO {
    
    private Long id;
    
    @NotBlank(message = "Naziv cilja je obavezan")
    private String name;
    
    @NotNull(message = "Ciljani iznos je obavezan")
    private BigDecimal targetAmount;
    
    private BigDecimal currentAmount;
    
    @NotNull(message = "Ciljani datum je obavezan")
    private Date targetDate;
    
    @NotNull(message = "Novčanik je obavezan")
    private Long walletId;
    
    private String walletName;
    private Date dateOfCreation;
    private Long userId;
    private boolean completed;
    private BigDecimal progressPercentage;
    private BigDecimal remainingAmount;
    private boolean overdue;
    private boolean onTrack;
    
    // Constructors
    public SavingsGoalDTO() {}
    
    public SavingsGoalDTO(Long id, String name, BigDecimal targetAmount, BigDecimal currentAmount,
                         Date targetDate, Long walletId, String walletName, Date dateOfCreation,
                         Long userId, boolean completed) {
        this.id = id;
        this.name = name;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
        this.walletId = walletId;
        this.walletName = walletName;
        this.dateOfCreation = dateOfCreation;
        this.userId = userId;
        this.completed = completed;
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
    
    public Long getWalletId() { return walletId; }
    public void setWalletId(Long walletId) { this.walletId = walletId; }
    
    public String getWalletName() { return walletName; }
    public void setWalletName(String walletName) { this.walletName = walletName; }
    
    public Date getDateOfCreation() { return dateOfCreation; }
    public void setDateOfCreation(Date dateOfCreation) { this.dateOfCreation = dateOfCreation; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    
    public BigDecimal getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(BigDecimal progressPercentage) { this.progressPercentage = progressPercentage; }
    
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    
    public boolean isOnTrack() { return onTrack; }
    public void setOnTrack(boolean onTrack) { this.onTrack = onTrack; }
}
