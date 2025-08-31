package com.example.WalletApp.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfTransaction;

    @Column(nullable = false)
    private boolean repeating = false;

    @Enumerated(EnumType.STRING)
    @Column
    private Frequency frequency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Constructors
    public Transaction() {
        this.dateOfTransaction = new Date();
    }

    public Transaction(String name, BigDecimal amount, TransactionType type, Category category, 
                      Wallet wallet, User user) {
        this();
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.wallet = wallet;
        this.user = user;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Date getDateOfTransaction() { return dateOfTransaction; }
    public void setDateOfTransaction(Date dateOfTransaction) { this.dateOfTransaction = dateOfTransaction; }

    public boolean isRepeating() { return repeating; }
    public void setRepeating(boolean repeating) { this.repeating = repeating; }

    public Frequency getFrequency() { return frequency; }
    public void setFrequency(Frequency frequency) { this.frequency = frequency; }

    public Wallet getWallet() { return wallet; }
    public void setWallet(Wallet wallet) { this.wallet = wallet; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Helper methods
    public boolean isIncome() {
        return this.type == TransactionType.INCOME;
    }

    public boolean isExpense() {
        return this.type == TransactionType.EXPENSE;
    }

    public void updateWalletBalance() {
        if (this.wallet != null) {
            if (this.isIncome()) {
                this.wallet.updateBalance(this.amount);
            } else {
                this.wallet.updateBalance(this.amount.negate());
            }
        }
    }
}

// Enum definitions
enum TransactionType {
    INCOME, EXPENSE
}

enum Frequency {
    WEEKLY, MONTHLY, QUARTERLY, YEARLY
}
