package com.example.WalletApp.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Entitet koji predstavlja korisnika u sistemu
 * 
 * Ova klasa mapira tabelu 'users' u bazi podataka i sadrži sve podatke
 * o korisniku. Koristi JPA anotacije za mapiranje u bazu podataka.
 * 
 * @author vuksta
 * @version 1.0
 */
@Entity
@Table(name = "users")
public class User implements Serializable {

    // Primarni ključ - automatski se generiše
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ime korisnika - obavezno polje
    @Column(nullable = false)
    private String firstName;

    // Prezime korisnika - obavezno polje
    @Column(nullable = false)
    private String lastName;

    // Korisničko ime - mora biti jedinstveno
    @Column(unique = true, nullable = false)
    private String username;

    // Email adresa - mora biti jedinstvena
    @Column(unique = true, nullable = false)
    private String email;

    // Lozinka korisnika - obavezno polje
    @Column(nullable = false)
    private String password;

    // Datum rođenja - čuva se samo datum bez vremena
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    private Date birthDate;

    // Uloga korisnika - enum vrednost
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Link ka profilnoj slici korisnika
    @Column
    private String imageLink;

    // Valuta koju korisnik koristi - veza sa Currency entitetom
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "currency_id")
    private Currency currency;

    // Datum registracije - automatski se postavlja
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateOfRegistration;

    // Status blokiranja korisnika - default je false
    @Column(nullable = false)
    private boolean blocked = false;

    // Veze sa drugim entitetima - JPA relacije
    
    // Novčanici korisnika - jedan korisnik može imati više novčanika
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Wallet> wallets = new HashSet<>();

    // Transakcije korisnika - jedan korisnik može imati više transakcija
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Transaction> transactions = new HashSet<>();

    // Kategorije korisnika - jedan korisnik može imati više kategorija
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Category> categories = new HashSet<>();

    // Ciljevi štednje korisnika - jedan korisnik može imati više ciljeva
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<SavingsGoal> savingsGoals = new HashSet<>();

    /**
     * Podrazumevani konstruktor
     * Automatski postavlja datum registracije na trenutni datum
     */
    public User() {
        this.dateOfRegistration = new Date();
    }

    /**
     * Konstruktor sa osnovnim podacima korisnika
     * 
     * @param firstName - ime korisnika
     * @param lastName - prezime korisnika
     * @param username - korisničko ime
     * @param email - email adresa
     * @param password - lozinka
     * @param birthDate - datum rođenja
     * @param role - uloga korisnika
     * @param currency - valuta korisnika
     */
    public User(String firstName, String lastName, String username, String email, String password, 
                Date birthDate, Role role, Currency currency) {
        this();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
        this.currency = currency;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }

    public Currency getCurrency() { return currency; }
    public void setCurrency(Currency currency) { this.currency = currency; }

    public Date getDateOfRegistration() { return dateOfRegistration; }
    public void setDateOfRegistration(Date dateOfRegistration) { this.dateOfRegistration = dateOfRegistration; }

    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }

    public Set<Wallet> getWallets() { return wallets; }
    public void setWallets(Set<Wallet> wallets) { this.wallets = wallets; }

    public Set<Transaction> getTransactions() { return transactions; }
    public void setTransactions(Set<Transaction> transactions) { this.transactions = transactions; }

    public Set<Category> getCategories() { return categories; }
    public void setCategories(Set<Category> categories) { this.categories = categories; }

    public Set<SavingsGoal> getSavingsGoals() { return savingsGoals; }
    public void setSavingsGoals(Set<SavingsGoal> savingsGoals) { this.savingsGoals = savingsGoals; }

    // Helper methods
    public void addWallet(Wallet wallet) {
        wallets.add(wallet);
        wallet.setUser(this);
    }

    public void removeWallet(Wallet wallet) {
        wallets.remove(wallet);
        wallet.setUser(null);
    }

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setUser(this);
    }

    public void addCategory(Category category) {
        categories.add(category);
        category.setUser(this);
    }

    public void addSavingsGoal(SavingsGoal savingsGoal) {
        savingsGoals.add(savingsGoal);
        savingsGoal.setUser(this);
    }
}
