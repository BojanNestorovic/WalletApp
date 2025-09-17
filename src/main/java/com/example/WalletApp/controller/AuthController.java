package com.example.WalletApp.controller;

import com.example.WalletApp.dto.LoginDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * Kontroler za autentifikaciju korisnika
 * 
 * Ovaj kontroler rukuje zahtevima vezanim za prijavu i registraciju korisnika.
 * Koristi se za kreiranje novih korisničkih naloga i validaciju postojećih.
 * 
 * @author vuksta
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * Registruje novog korisnika u sistem
     * 
     * Prima UserDTO objekat sa podacima korisnika, konvertuje ga u entitet,
     * registruje korisnika i vraća kreiranog korisnika kao DTO.
     * 
     * @param userDTO - podaci novog korisnika
     * @return ResponseEntity sa kreiranim korisnikom ili greškom
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO) {
        try {
            // Konvertuje DTO u entitet
            User user = userService.convertToEntity(userDTO);
            // Registruje korisnika
            User registeredUser = userService.registerUser(user);
            // Konvertuje entitet nazad u DTO za odgovor
            UserDTO responseDTO = userService.convertToDTO(registeredUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error registering user: " + e.getMessage());
        }
    }

    /**
     * Prijavljuje korisnika u sistem
     * 
     * Validira korisničke podatke (korisničko ime i lozinku) i vraća
     * odgovarajući HTTP status kod. U realnoj aplikaciji bi se koristio JWT token.
     * 
     * @param loginDTO - podaci za prijavu (korisničko ime i lozinka)
     * @return ResponseEntity sa statusom prijave
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            // Validira korisničke podatke
            boolean isValid = userService.validateUserCredentials(loginDTO.getUsername(), loginDTO.getPassword());
            if (isValid) {
                return ResponseEntity.ok().body("Login successful");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error during login: " + e.getMessage());
        }
    }
}
