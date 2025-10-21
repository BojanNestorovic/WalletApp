package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.AdminNote;
import com.example.WalletApp.service.AdminService;
import com.example.WalletApp.service.CategoryService;
import com.example.WalletApp.service.TransactionService;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Administrator operations.
 * Includes user management, monitoring, notes, and dashboard metrics.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Check if current user is admin (helper method for authorization).
     */
    private boolean isAdmin(HttpSession session) {
        UserDTO user = (UserDTO) session.getAttribute("user");
        return user != null && "ADMINISTRATOR".equals(user.getRole());
    }
    
    /**
     * Get all users (admin only).
     * GET /api/admin/users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers(HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            List<UserDTO> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Block/Unblock user (admin only).
     * PUT /api/admin/users/{id}/toggle-block
     */
    @PutMapping("/users/{id}/toggle-block")
    public ResponseEntity<?> toggleBlockUser(@PathVariable Long id, HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            UserDTO user = userService.toggleBlockUser(id);
            return ResponseEntity.ok(user);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Add admin note to user (admin only).
     * POST /api/admin/users/{id}/notes
     */
    @PostMapping("/users/{id}/notes")
    public ResponseEntity<?> addNoteToUser(@PathVariable Long id, 
                                          @RequestBody Map<String, String> noteData,
                                          HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            UserDTO admin = (UserDTO) session.getAttribute("user");
            String noteText = noteData.get("note");
            
            if (noteText == null || noteText.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("error", "Tekst beleške je obavezan"));
            }
            
            AdminNote note = adminService.addNoteToUser(id, admin.getId(), noteText);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", note.getId());
            response.put("note", note.getNote());
            response.put("dateCreated", note.getDateCreated());
            response.put("adminUsername", admin.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all notes for a user (admin only).
     * GET /api/admin/users/{id}/notes
     */
    @GetMapping("/users/{id}/notes")
    public ResponseEntity<?> getUserNotes(@PathVariable Long id, HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            List<AdminNote> notes = adminService.getUserNotes(id);
            
            List<Map<String, Object>> notesResponse = notes.stream()
                    .map(note -> {
                        Map<String, Object> noteMap = new HashMap<>();
                        noteMap.put("id", note.getId());
                        noteMap.put("note", note.getNote());
                        noteMap.put("dateCreated", note.getDateCreated());
                        noteMap.put("adminUsername", note.getAdmin().getUsername());
                        return noteMap;
                    })
                    .toList();
            
            return ResponseEntity.ok(notesResponse);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all transactions in system (admin only).
     * GET /api/admin/transactions
     */
    @GetMapping("/transactions")
    public ResponseEntity<?> getAllTransactions(HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            return ResponseEntity.ok(transactionService.getAllTransactions());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get dashboard metrics (admin only).
     * GET /api/admin/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardMetrics(HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            Map<String, Object> metrics = adminService.getDashboardMetrics();
            return ResponseEntity.ok(metrics);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create predefined category (admin only).
     * POST /api/admin/categories
     */
    @PostMapping("/categories")
    public ResponseEntity<?> createPredefinedCategory(@Valid @RequestBody CategoryDTO categoryDTO,
                                                      HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CategoryDTO created = categoryService.createPredefinedCategory(categoryDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete predefined category (admin only).
     * DELETE /api/admin/categories/{id}
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<?> deletePredefinedCategory(@PathVariable Long id, HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            categoryService.deletePredefinedCategory(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
