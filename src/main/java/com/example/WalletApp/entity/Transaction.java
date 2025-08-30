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





}
