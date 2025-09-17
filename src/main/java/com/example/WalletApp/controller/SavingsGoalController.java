package com.example.WalletApp.controller;

import com.example.WalletApp.dto.SavingsGoalDTO;
import com.example.WalletApp.entity.SavingsGoal;
import com.example.WalletApp.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/savings-goals")
@CrossOrigin(origins = "*")
public class SavingsGoalController {

    @Autowired
    private SavingsGoalService savingsGoalService;

    // Get all savings goals
    @GetMapping
    public ResponseEntity<List<SavingsGoalDTO>> getAllSavingsGoals() {
        List<SavingsGoal> savingsGoals = savingsGoalService.getAllSavingsGoals();
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get all savings goals with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<SavingsGoalDTO>> getAllSavingsGoals(Pageable pageable) {
        Page<SavingsGoal> savingsGoals = savingsGoalService.getAllSavingsGoals(pageable);
        Page<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.map(savingsGoalService::convertToDTO);
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get savings goal by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSavingsGoalById(@PathVariable Long id) {
        Optional<SavingsGoal> savingsGoal = savingsGoalService.getSavingsGoalById(id);
        if (savingsGoal.isPresent()) {
            SavingsGoalDTO savingsGoalDTO = savingsGoalService.convertToDTO(savingsGoal.get());
            return ResponseEntity.ok(savingsGoalDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new savings goal
    @PostMapping
    public ResponseEntity<?> createSavingsGoal(@Valid @RequestBody SavingsGoalDTO savingsGoalDTO) {
        try {
            SavingsGoal savingsGoal = savingsGoalService.convertToEntity(savingsGoalDTO);
            SavingsGoal createdSavingsGoal = savingsGoalService.createSavingsGoal(savingsGoal);
            SavingsGoalDTO responseDTO = savingsGoalService.convertToDTO(createdSavingsGoal);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating savings goal: " + e.getMessage());
        }
    }

    // Update savings goal
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSavingsGoal(@PathVariable Long id, @Valid @RequestBody SavingsGoalDTO savingsGoalDTO) {
        try {
            SavingsGoal savingsGoal = savingsGoalService.convertToEntity(savingsGoalDTO);
            SavingsGoal updatedSavingsGoal = savingsGoalService.updateSavingsGoal(id, savingsGoal);
            SavingsGoalDTO responseDTO = savingsGoalService.convertToDTO(updatedSavingsGoal);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating savings goal: " + e.getMessage());
        }
    }

    // Delete savings goal
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSavingsGoal(@PathVariable Long id) {
        try {
            savingsGoalService.deleteSavingsGoal(id);
            return ResponseEntity.ok().body("Savings goal deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting savings goal: " + e.getMessage());
        }
    }

    // Get savings goals by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsGoalDTO>> getSavingsGoalsByUserId(@PathVariable Long userId) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getSavingsGoalsByUserId(userId);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get savings goals by wallet ID
    @GetMapping("/wallet/{walletId}")
    public ResponseEntity<List<SavingsGoalDTO>> getSavingsGoalsByWalletId(@PathVariable Long walletId) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getSavingsGoalsByWalletId(walletId);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get completed savings goals by user ID
    @GetMapping("/user/{userId}/completed")
    public ResponseEntity<List<SavingsGoalDTO>> getCompletedSavingsGoals(@PathVariable Long userId) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getCompletedSavingsGoals(userId);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get active savings goals by user ID
    @GetMapping("/user/{userId}/active")
    public ResponseEntity<List<SavingsGoalDTO>> getActiveSavingsGoals(@PathVariable Long userId) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getActiveSavingsGoals(userId);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Add amount to savings goal
    @PostMapping("/{id}/add")
    public ResponseEntity<?> addToSavingsGoal(@PathVariable Long id, @RequestParam BigDecimal amount) {
        try {
            savingsGoalService.addToSavingsGoal(id, amount);
            return ResponseEntity.ok().body("Amount added to savings goal successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding amount: " + e.getMessage());
        }
    }

    // Complete savings goal
    @PostMapping("/{id}/complete")
    public ResponseEntity<?> completeSavingsGoal(@PathVariable Long id) {
        try {
            savingsGoalService.completeSavingsGoal(id);
            return ResponseEntity.ok().body("Savings goal completed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error completing savings goal: " + e.getMessage());
        }
    }

    // Get overdue goals by user ID
    @GetMapping("/user/{userId}/overdue")
    public ResponseEntity<List<SavingsGoalDTO>> getOverdueGoals(@PathVariable Long userId) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getOverdueGoals(userId);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get goals by date range
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<SavingsGoalDTO>> getGoalsByDateRange(
            @PathVariable Long userId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<SavingsGoal> savingsGoals = savingsGoalService.getGoalsByDateRange(userId, startDate, endDate);
        List<SavingsGoalDTO> savingsGoalDTOs = savingsGoals.stream()
            .map(savingsGoalService::convertToDTO)
            .toList();
        return ResponseEntity.ok(savingsGoalDTOs);
    }

    // Get savings statistics for user
    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<?> getSavingsStatsByUserId(@PathVariable Long userId) {
        BigDecimal totalSavings = savingsGoalService.getTotalSavingsByUserId(userId);
        BigDecimal totalTargetAmount = savingsGoalService.getTotalTargetAmountByUserId(userId);
        
        return ResponseEntity.ok().body(String.format(
            "Total Savings: %s, Total Target Amount: %s", 
            totalSavings, totalTargetAmount));
    }
}
