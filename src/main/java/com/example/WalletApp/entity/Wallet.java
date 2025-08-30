package com.example.WalletApp.entity;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private Long beginState;
    @Column
    private Long currentState;

    @OneToOne(mappedBy = "currency",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Currency currency;

    @Column
    private Date dateOfCreation;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private User user;

    //link to transaction.java

    @OneToMany(mappedBy = "wallet", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    //link to

    @Column
    private boolean savingsAccount;
    @Column
    private boolean archived;

}
