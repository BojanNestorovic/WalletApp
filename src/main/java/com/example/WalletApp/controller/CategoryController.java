package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Category management.
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    /**
     * Get all predefined categories.
     * GET /api/categories/predefined
     */
    @GetMapping("/predefined")
    public ResponseEntity<?> getPredefinedCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getPredefinedCategories();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all available categories for user (predefined + custom).
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<?> getAllAvailableCategories(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<CategoryDTO> categories = categoryService.getAllAvailableCategories(user.getId());
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's custom categories.
     * GET /api/categories/custom
     */
    @GetMapping("/custom")
    public ResponseEntity<?> getUserCategories(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<CategoryDTO> categories = categoryService.getUserCategories(user.getId());
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Create custom category.
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO, 
                                           HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            CategoryDTO created = categoryService.createCategory(categoryDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete custom category.
     * DELETE /api/categories/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            categoryService.deleteCategory(id, user.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
