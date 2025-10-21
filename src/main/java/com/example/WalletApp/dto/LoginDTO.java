package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;

public class LoginDTO {
    
    @NotBlank(message = "Korisničko ime je obavezno")
    private String username;
    
    @NotBlank(message = "Lozinka je obavezna")
    private String password;
    
    public LoginDTO() {}
    
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
