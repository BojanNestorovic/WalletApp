package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.dto.CurrencyDTO;
import com.example.WalletApp.dto.TransactionDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.AdminNote;
import com.example.WalletApp.service.AdminService;
import com.example.WalletApp.service.CategoryService;
import com.example.WalletApp.service.CurrencyService;
import com.example.WalletApp.service.TransactionService;
import com.example.WalletApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    
    @Autowired
    private CurrencyService currencyService;
    
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
     * Get all categories (admin only).
     * GET /api/admin/categories
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAllCategories(HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            return ResponseEntity.ok(categoryService.getAllCategories());
            
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
    
    /**
     * Search and filter transactions (admin only).
     * GET /api/admin/transactions/search?user=&category=&minAmount=&maxAmount=&startDate=&endDate=&sortBy=&sortOrder=
     */
    @GetMapping("/transactions/search")
    public ResponseEntity<?> searchTransactions(@RequestParam(required = false) String user,
                                              @RequestParam(required = false) String category,
                                              @RequestParam(required = false) BigDecimal minAmount,
                                              @RequestParam(required = false) BigDecimal maxAmount,
                                              @RequestParam(required = false) String startDate,
                                              @RequestParam(required = false) String endDate,
                                              @RequestParam(defaultValue = "dateCreated") String sortBy,
                                              @RequestParam(defaultValue = "desc") String sortOrder,
                                              HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            // Parse dates if provided
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = startDate != null && !startDate.isEmpty() ? 
                sdf.parse(startDate) : null;
            Date end = endDate != null && !endDate.isEmpty() ? 
                sdf.parse(endDate) : null;
            
            List<TransactionDTO> results = transactionService.searchTransactions(
                user, category, minAmount, maxAmount, start, end, sortBy, sortOrder);
            
            return ResponseEntity.ok(results);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all currencies (admin only).
     * GET /api/admin/currencies
     */
    @GetMapping("/currencies")
    public ResponseEntity<?> getAllCurrencies(HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            return ResponseEntity.ok(currencyService.getAllCurrencies());
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create new currency (admin only).
     * POST /api/admin/currencies
     */
    @PostMapping("/currencies")
    public ResponseEntity<?> createCurrency(@Valid @RequestBody CurrencyDTO currencyDTO,
                                          HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO created = currencyService.createCurrency(currencyDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update currency (admin only).
     * PUT /api/admin/currencies/{id}
     */
    @PutMapping("/currencies/{id}")
    public ResponseEntity<?> updateCurrency(@PathVariable Long id, 
                                          @Valid @RequestBody CurrencyDTO currencyDTO,
                                          HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO updated = currencyService.updateCurrency(id, currencyDTO);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update currency from Frankfurter API (admin only).
     * PUT /api/admin/currencies/{id}/update-from-api
     */
    @PutMapping("/currencies/{id}/update-from-api")
    public ResponseEntity<?> updateCurrencyFromAPI(@PathVariable Long id, HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            CurrencyDTO updated = currencyService.updateCurrencyFromAPI(id);
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete currency (admin only).
     * DELETE /api/admin/currencies/{id}
     */
    @DeleteMapping("/currencies/{id}")
    public ResponseEntity<?> deleteCurrency(@PathVariable Long id, HttpSession session) {
        try {
            if (!isAdmin(session)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Nemate pristup ovoj akciji"));
            }
            
            currencyService.deleteCurrency(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
