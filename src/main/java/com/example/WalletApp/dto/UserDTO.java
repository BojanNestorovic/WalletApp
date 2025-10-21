package com.example.WalletApp.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

public class UserDTO {
    
    private Long id;
    
    @NotBlank(message = "Ime je obavezno")
    private String firstName;
    
    @NotBlank(message = "Prezime je obavezno")
    private String lastName;
    
    @NotBlank(message = "Korisničko ime je obavezno")
    private String username;
    
    @NotBlank(message = "Email je obavezan")
    @Email(message = "Email mora biti validna email adresa")
    private String email;
    
    private String password;
    
    @NotNull(message = "Datum rođenja je obavezan")
    private Date birthDate;
    
    private String role;
    private String imageLink;
    private Long currencyId;
    private String currencyName;
    private Date dateOfRegistration;
    private boolean blocked;
    
    // Constructors
    public UserDTO() {}
    
    public UserDTO(Long id, String firstName, String lastName, String username, String email,
                   String role, String imageLink, Long currencyId, String currencyName,
                   Date dateOfRegistration, boolean blocked, Date birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.role = role;
        this.imageLink = imageLink;
        this.currencyId = currencyId;
        this.currencyName = currencyName;
        this.dateOfRegistration = dateOfRegistration;
        this.blocked = blocked;
        this.birthDate = birthDate;
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
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public String getImageLink() { return imageLink; }
    public void setImageLink(String imageLink) { this.imageLink = imageLink; }
    
    public Long getCurrencyId() { return currencyId; }
    public void setCurrencyId(Long currencyId) { this.currencyId = currencyId; }
    
    public String getCurrencyName() { return currencyName; }
    public void setCurrencyName(String currencyName) { this.currencyName = currencyName; }
    
    public Date getDateOfRegistration() { return dateOfRegistration; }
    public void setDateOfRegistration(Date dateOfRegistration) { this.dateOfRegistration = dateOfRegistration; }
    
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
