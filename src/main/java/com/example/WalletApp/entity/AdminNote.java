package com.example.WalletApp.entity;

import javax.persistence.*;
import java.util.Date;

/**
 * Entity representing admin notes about users.
 * Only visible to administrators.
 */
@Entity
@Table(name = "admin_notes")
public class AdminNote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    
    @Column(nullable = false, length = 1000)
    private String note;
    
    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;
    
    // Constructors
    public AdminNote() {
        this.dateCreated = new Date();
    }
    
    public AdminNote(User user, User admin, String note) {
        this();
        this.user = user;
        this.admin = admin;
        this.note = note;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public User getAdmin() { return admin; }
    public void setAdmin(User admin) { this.admin = admin; }
    
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    
    public Date getDateCreated() { return dateCreated; }
    public void setDateCreated(Date dateCreated) { this.dateCreated = dateCreated; }
}
