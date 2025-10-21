package com.example.WalletApp.service;

import com.example.WalletApp.dto.LoginDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for User management with business logic and session handling.
 * Uses @Transactional for atomic operations.
 */
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CurrencyRepository currencyRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * Register a new user with encrypted password.
     */
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // Validate username and email uniqueness
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new RuntimeException("Korisničko ime već postoji");
        }
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("Email već postoji");
        }
        
        User user = new User();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setUsername(userDTO.getUsername());
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword())); // BCrypt
        user.setBirthDate(userDTO.getBirthDate());
        user.setRole(com.example.WalletApp.entity.Role.USER);
        
        // Set default currency if provided
        if (userDTO.getCurrencyId() != null) {
            Currency currency = currencyRepository.findById(userDTO.getCurrencyId())
                    .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
            user.setCurrency(currency);
        }
        
        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }
    
    /**
     * Authenticate user with username and password.
     */
    public UserDTO login(LoginDTO loginDTO) {
        Optional<User> userOpt = userRepository.findByUsername(loginDTO.getUsername());
        if (!userOpt.isPresent()) {
            throw new RuntimeException("Pogrešno korisničko ime ili lozinka");
        }
        
        User user = userOpt.get();
        if (user.isBlocked()) {
            throw new RuntimeException("Korisnik je blokiran");
        }
        
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Pogrešno korisničko ime ili lozinka");
        }
        
        return convertToDTO(user);
    }
    
    /**
     * Get user by ID.
     */
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        return convertToDTO(user);
    }
    
    /**
     * Get all users (admin only).
     */
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Update user profile.
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO userDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmail(userDTO.getEmail());
        user.setImageLink(userDTO.getImageLink());
        
        if (userDTO.getCurrencyId() != null) {
            Currency currency = currencyRepository.findById(userDTO.getCurrencyId())
                    .orElseThrow(() -> new RuntimeException("Valuta ne postoji"));
            user.setCurrency(currency);
        }
        
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    /**
     * Block or unblock user (admin only).
     */
    @Transactional
    public UserDTO toggleBlockUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        user.setBlocked(!user.isBlocked());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
    
    /**
     * Get total user count.
     */
    public long getTotalUserCount() {
        return userRepository.count();
    }
    
    /**
     * Get active users count (not blocked).
     */
    public long getActiveUserCount() {
        return userRepository.countActiveUsers();
    }
    
    /**
     * Get users registered after a specific date.
     */
    public List<UserDTO> getUsersRegisteredAfter(Date date) {
        return userRepository.findUsersRegisteredAfter(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert User entity to UserDTO.
     */
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
            user.getId(),
            user.getFirstName(),
            user.getLastName(),
            user.getUsername(),
            user.getEmail(),
            user.getRole().toString(),
            user.getImageLink(),
            user.getCurrency() != null ? user.getCurrency().getId() : null,
            user.getCurrency() != null ? user.getCurrency().getName() : null,
            user.getDateOfRegistration(),
            user.isBlocked(),
            user.getBirthDate()
        );
    }
}
