package com.example.WalletApp.controller;

import com.example.WalletApp.dto.LoginDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * RESTful Controller for Authentication using HttpSession.
 * POST for creating resources, GET for reading.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Register a new user.
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDTO userDTO, HttpSession session) {
        try {
            UserDTO registeredUser = userService.registerUser(userDTO);
            
            // Store user in session
            session.setAttribute("user", registeredUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Registracija uspešna");
            response.put("user", registeredUser);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    /**
     * Login user with HttpSession.
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginDTO loginDTO, HttpSession session) {
        try {
            UserDTO user = userService.login(loginDTO);
            
            // Store user in session using setAttribute()
            session.setAttribute("user", user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Prijava uspešna");
            response.put("user", user);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }
    
    /**
     * Get current logged-in user from session.
     * GET /api/auth/current
     */
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Niste prijavljeni");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        
        return ResponseEntity.ok(user);
    }
    
    /**
     * Logout user by invalidating session.
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpSession session) {
        session.invalidate();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Odjava uspešna");
        
        return ResponseEntity.ok(response);
    }
}
