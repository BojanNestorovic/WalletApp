package com.example.WalletApp.entity;

import javax.persistence.*;

@Entity
public class Currency {

    @Id
    @Column
    private Long id;

    @Column
    private String name;
    @Column
    private Double priceToEur;
}
