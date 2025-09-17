package com.example.WalletApp.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date targetDate;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfCreation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean completed = false;

    // Constructors
    public SavingsGoal() {
        this.dateOfCreation = new Date();
        this.currentAmount = BigDecimal.ZERO;
    }

    public SavingsGoal(String name, BigDecimal targetAmount, Date targetDate, Wallet wallet, User user) {
        this();
        this.name = name;
        this.targetAmount = targetAmount;
        this.targetDate = targetDate;
        this.wallet = wallet;
        this.user = user;
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

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    // Helper methods
    public BigDecimal getProgressPercentage() {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP)
                           .multiply(new BigDecimal("100"));
    }

    public BigDecimal getRemainingAmount() {
        return targetAmount.subtract(currentAmount);
    }

    public boolean isOverdue() {
        return new Date().after(targetDate) && !completed;
    }

    public void addAmount(BigDecimal amount) {
        this.currentAmount = this.currentAmount.add(amount);
        if (this.currentAmount.compareTo(this.targetAmount) >= 0) {
            this.completed = true;
        }
    }

    public boolean isOnTrack() {
        if (completed) return true;
        
        Date now = new Date();
        long totalDays = (targetDate.getTime() - dateOfCreation.getTime()) / (1000 * 60 * 60 * 24);
        long elapsedDays = (now.getTime() - dateOfCreation.getTime()) / (1000 * 60 * 60 * 24);
        
        if (totalDays <= 0) return false;
        
        BigDecimal expectedProgress = new BigDecimal(elapsedDays)
            .divide(new BigDecimal(totalDays), 4, RoundingMode.HALF_UP);
        BigDecimal actualProgress = currentAmount.divide(targetAmount, 4, RoundingMode.HALF_UP);
        
        return actualProgress.compareTo(expectedProgress) >= 0;
    }
}
