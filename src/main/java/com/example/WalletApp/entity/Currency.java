package com.example.WalletApp.entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Currency {

    @Id
    @Column
    private Long id;

    @Column
    private String name;
    @Column
    private Double priceToEur;

    @OneToMany(mappedBy = "currency", fetch = FetchType.LAZY)
    private Set<Wallet>  wallets = new HashSet<>();

    public Currency() {}

    public Currency(String name, double priceToEur) {
        this.name = name;
        this.priceToEur = priceToEur;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPriceToEur() { return priceToEur; }
    public void setPriceToEur(double priceToEur) { this.priceToEur = priceToEur; }

    public Set<Wallet> getWallets() { return wallets; }
    public void setWallets(Set<Wallet> wallets) { this.wallets = wallets; }
}
