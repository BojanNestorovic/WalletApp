package com.example.WalletApp.entity;

import java.awt.*;
import java.io.IO;
import javax.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

enum Role{

    COSTUMER, ADMINISTRATOR

}
enum Val{

    RSD, EUR, USD

}
@Entity
public class User  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String username;

    @Column
    private String email;

    @Column
    private String password;

    @Column
    private Date birthDate;

    @Column
    private Role role;

    @Column
    private String image_link;

    @Column
    private Val currency;

    @Column
    private Date dateOfRegistration;

    @Column
    private boolean blocked;

    //link to wallet
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL )
    private Set<Wallet> wallets =  new HashSet<>();

    //link to transaction.java
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();





}
