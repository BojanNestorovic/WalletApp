package com.example.WalletApp.controller;

import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST kontroler za upravljanje korisnicima
 * 
 * Ovaj kontroler pruža API endpoint-e za:
 * - Dohvatanje svih korisnika
 * - Dohvatanje korisnika po ID-u
 * - Kreiranje novih korisnika
 * - Ažuriranje postojećih korisnika
 * - Brisanje korisnika
 * - Pretragu korisnika sa paginacijom
 * - Upravljanje statusom korisnika
 * 
 * @author vuksta
 * @version 1.0
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDTOs = users.stream()
            .map(userService::convertToDTO)
            .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Get all users with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);
        Page<UserDTO> userDTOs = users.map(userService::convertToDTO);
        return ResponseEntity.ok(userDTOs);
    }

    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.getUserById(id);
        if (user.isPresent()) {
            UserDTO userDTO = userService.convertToDTO(user.get());
            return ResponseEntity.ok(userDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.convertToEntity(userDTO);
            User updatedUser = userService.updateUser(id, user);
            UserDTO responseDTO = userService.convertToDTO(updatedUser);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user: " + e.getMessage());
        }
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().body("User deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting user: " + e.getMessage());
        }
    }

    // Get users by role
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<User> users = userService.getUsersByRole(role);
        List<UserDTO> userDTOs = users.stream()
            .map(userService::convertToDTO)
            .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Get blocked users
    @GetMapping("/blocked")
    public ResponseEntity<List<UserDTO>> getBlockedUsers() {
        List<User> users = userService.getBlockedUsers();
        List<UserDTO> userDTOs = users.stream()
            .map(userService::convertToDTO)
            .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Get active users
    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<User> users = userService.getActiveUsers();
        List<UserDTO> userDTOs = users.stream()
            .map(userService::convertToDTO)
            .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Get user statistics
    @GetMapping("/stats")
    public ResponseEntity<?> getUserStats() {
        long totalUsers = userService.getTotalUserCount();
        long activeUsers = userService.getActiveUserCount();
        long blockedUsers = totalUsers - activeUsers;
        
        return ResponseEntity.ok().body(String.format(
            "Total Users: %d, Active Users: %d, Blocked Users: %d", 
            totalUsers, activeUsers, blockedUsers));
    }

    // Search users by name
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<User> users = userService.searchUsersByName(name);
        List<UserDTO> userDTOs = users.stream()
            .map(userService::convertToDTO)
            .toList();
        return ResponseEntity.ok(userDTOs);
    }

    // Block user
    @PostMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            userService.blockUser(id);
            return ResponseEntity.ok().body("User blocked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error blocking user: " + e.getMessage());
        }
    }

    // Unblock user
    @PostMapping("/{id}/unblock")
    public ResponseEntity<?> unblockUser(@PathVariable Long id) {
        try {
            userService.unblockUser(id);
            return ResponseEntity.ok().body("User unblocked successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error unblocking user: " + e.getMessage());
        }
    }

    // Update user currency
    @PutMapping("/{id}/currency")
    public ResponseEntity<?> updateUserCurrency(@PathVariable Long id, @RequestParam Long currencyId) {
        try {
            userService.updateUserCurrency(id, currencyId);
            return ResponseEntity.ok().body("User currency updated successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user currency: " + e.getMessage());
        }
    }
}
