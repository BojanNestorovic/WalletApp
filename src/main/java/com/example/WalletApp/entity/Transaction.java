package com.example.WalletApp.entity;

import javax.persistence.*;
import java.util.Date;

enum TransactionType{

    INCOME, EXPENSE

}

enum Consistency{

    WEEKLY, MONTHLY, QUARTERLY, YEARLY, NONE

}

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private double amount;

    @Column
    private TransactionType type;

    @Column
    private Date dateOfTransaction;

    @Column
    private boolean repeating;


    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Wallet wallet;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    @Column
    private Consistency consistency;

    //getters and setters

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public Date getDateOfTransaction() {
        return dateOfTransaction;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public User getUser() {
        return user;
    }

    public Consistency getCosnsistency() {
        return consistency;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setDateOfTransaction(Date dateOfTransaction) {
        this.dateOfTransaction = dateOfTransaction;
    }

    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setConsistency(Consistency consistency) {
        this.consistency = consistency;
    }
}
