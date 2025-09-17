package com.example.WalletApp.service;

import com.example.WalletApp.dto.SavingsGoalDTO;
import com.example.WalletApp.entity.SavingsGoal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface SavingsGoalService {
    
    // CRUD Operations
    SavingsGoal createSavingsGoal(SavingsGoal savingsGoal);
    SavingsGoal updateSavingsGoal(Long id, SavingsGoal savingsGoal);
    void deleteSavingsGoal(Long id);
    Optional<SavingsGoal> getSavingsGoalById(Long id);
    List<SavingsGoal> getAllSavingsGoals();
    Page<SavingsGoal> getAllSavingsGoals(Pageable pageable);
    
    // User-specific operations
    List<SavingsGoal> getSavingsGoalsByUserId(Long userId);
    List<SavingsGoal> getSavingsGoalsByWalletId(Long walletId);
    List<SavingsGoal> getCompletedSavingsGoals(Long userId);
    List<SavingsGoal> getActiveSavingsGoals(Long userId);
    
    // Goal management
    void addToSavingsGoal(Long goalId, BigDecimal amount);
    void completeSavingsGoal(Long goalId);
    List<SavingsGoal> getOverdueGoals(Long userId);
    List<SavingsGoal> getGoalsByDateRange(Long userId, Date startDate, Date endDate);
    
    // Statistics
    BigDecimal getTotalSavingsByUserId(Long userId);
    BigDecimal getTotalTargetAmountByUserId(Long userId);
    
    // DTO Conversion
    SavingsGoalDTO convertToDTO(SavingsGoal savingsGoal);
    SavingsGoal convertToEntity(SavingsGoalDTO savingsGoalDTO);
}
