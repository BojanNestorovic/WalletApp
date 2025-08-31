package com.example.WalletApp.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private Double valueToEur;

    @OneToMany(mappedBy = "currency", fetch = FetchType.LAZY)
    private Set<Wallet> wallets = new HashSet<>();

    @OneToMany(mappedBy = "currency", fetch = FetchType.LAZY)
    private Set<User> users = new HashSet<>();

    // Constructors
    public Currency() {}

    public Currency(String name, Double valueToEur) {
        this.name = name;
        this.valueToEur = valueToEur;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getValueToEur() { return valueToEur; }
    public void setValueToEur(Double valueToEur) { this.valueToEur = valueToEur; }

    public Set<Wallet> getWallets() { return wallets; }
    public void setWallets(Set<Wallet> wallets) { this.wallets = wallets; }

    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }

    // Helper methods
    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
        wallet.setCurrency(this);
    }

    public void removeWallet(Wallet wallet) {
        wallets.remove(wallet);
        wallet.setCurrency(null);
    }

    public void addUser(User user) {
        users.add(user);
        user.setCurrency(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setCurrency(null);
    }
}
