package com.example.WalletApp.service;

import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {
    
    // CRUD Operations
    User registerUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    Page<User> getAllUsers(Pageable pageable);
    
    // Authentication
    boolean validateUserCredentials(String username, String password);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    // Business Logic
    List<User> getUsersByRole(String role);
    List<User> getBlockedUsers();
    List<User> getActiveUsers();
    long getTotalUserCount();
    long getActiveUserCount();
    List<User> getUsersRegisteredAfter(java.util.Date date);
    List<User> searchUsersByName(String searchTerm);
    
    // User Management
    void blockUser(Long userId);
    void unblockUser(Long userId);
    void updateUserCurrency(Long userId, Long currencyId);
    
    // DTO Conversion
    UserDTO convertToDTO(User user);
    User convertToEntity(UserDTO userDTO);
}
