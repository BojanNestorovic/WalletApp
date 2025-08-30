package com.example.WalletApp.entity;


import javax.persistence.*;

enum CategoryType{

    Income, Expense

}
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private CategoryType type;
    @Column(nullable = false)
    private boolean predefined;

    @ManyToOne(optional = true)
    @JoinColumn(name = "id", nullable = true)
    private User user;

    //constructors, getters and setters

    public Category() {}

    public Category(String name, CategoryType type, boolean predefined, User user) {
        this.name = name;
        this.type = type;
        this.predefined = predefined;
        this.user = user;
    }

    // === Getters and Setters ===
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }

    public boolean isPredefined() { return predefined; }
    public void setPredefined(boolean predefined) { this.predefined = predefined; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }



}
