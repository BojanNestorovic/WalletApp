package com.example.WalletApp.controller;

import com.example.WalletApp.dto.SavingsGoalDTO;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * RESTful Controller for Savings Goal management.
 */
@RestController
@RequestMapping("/savings-goals")
public class SavingsGoalController {
    
    @Autowired
    private SavingsGoalService savingsGoalService;
    
    /**
     * Create a new savings goal.
     * POST /api/savings-goals
     */
    @PostMapping
    public ResponseEntity<?> createSavingsGoal(@Valid @RequestBody SavingsGoalDTO goalDTO, 
                                               HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            SavingsGoalDTO created = savingsGoalService.createSavingsGoal(goalDTO, user.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all savings goals for current user.
     * GET /api/savings-goals
     */
    @GetMapping
    public ResponseEntity<?> getUserSavingsGoals(HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            List<SavingsGoalDTO> goals = savingsGoalService.getUserSavingsGoals(user.getId());
            return ResponseEntity.ok(goals);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get savings goal by ID.
     * GET /api/savings-goals/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSavingsGoalById(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            SavingsGoalDTO goal = savingsGoalService.getSavingsGoalById(id, user.getId());
            return ResponseEntity.ok(goal);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update savings goal progress.
     * PUT /api/savings-goals/{id}/progress
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            SavingsGoalDTO updated = savingsGoalService.updateProgress(id, user.getId());
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update savings goal details.
     * PUT /api/savings-goals/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSavingsGoal(@PathVariable Long id, 
                                              @Valid @RequestBody SavingsGoalDTO goalDTO,
                                              HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            SavingsGoalDTO updated = savingsGoalService.updateSavingsGoal(id, goalDTO, user.getId());
            return ResponseEntity.ok(updated);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete savings goal.
     * DELETE /api/savings-goals/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingsGoal(@PathVariable Long id, HttpSession session) {
        try {
            UserDTO user = (UserDTO) session.getAttribute("user");
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Niste prijavljeni"));
            }
            
            savingsGoalService.deleteSavingsGoal(id, user.getId());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
