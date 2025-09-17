package com.example.WalletApp.dto;

import javax.validation.constraints.*;
import java.util.Date;

/**
 * Data Transfer Object (DTO) za korisnika
 * 
 * DTO se koristi za prenos podataka između slojeva aplikacije.
 * Sadrži validacione anotacije koje proveravaju ispravnost podataka
 * pre slanja u bazu podataka.
 * 
 * @author vuksta
 * @version 1.0
 */
public class UserDTO {
    private Long id;
    
    // Ime korisnika - obavezno polje sa validacijom dužine
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String firstName;
    
    // Prezime korisnika - obavezno polje sa validacijom dužine
    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    private String lastName;
    
    // Korisničko ime - mora biti jedinstveno u sistemu
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    private String username;
    
    // Email adresa - mora biti u ispravnom formatu
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    // Lozinka - minimum 6 karaktera za sigurnost
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;
    
    // Datum rođenja - obavezno polje
    @NotNull(message = "Birth date is required")
    private Date birthDate;
    
    // Uloga korisnika (USER, ADMINISTRATOR)
    private String role;
    
    // Link ka profilnoj slici korisnika
    private String imageLink;
    
    // ID valute koju korisnik koristi
    private Long currencyId;
    
    // Datum registracije - automatski se postavlja
    private Date dateOfRegistration;
    
    // Status blokiranja korisnika
    private boolean blocked;
    
    /**
     * Podrazumevani konstruktor
     */
    public UserDTO() {}
    
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
     */
    public UserDTO(String firstName, String lastName, String username, String email, 
                   String password, Date birthDate, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.birthDate = birthDate;
        this.role = role;
    }
    
    // Getters and Setters - omogućavaju pristup privatnim poljima
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
    
    public Date getDateOfRegistration() { return dateOfRegistration; }
    public void setDateOfRegistration(Date dateOfRegistration) { this.dateOfRegistration = dateOfRegistration; }
    
    public boolean isBlocked() { return blocked; }
    public void setBlocked(boolean blocked) { this.blocked = blocked; }
}
