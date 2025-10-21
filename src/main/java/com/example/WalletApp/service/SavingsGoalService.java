package com.example.WalletApp.service;

import com.example.WalletApp.dto.SavingsGoalDTO;
import com.example.WalletApp.entity.SavingsGoal;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.SavingsGoalRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Savings Goal management with progress tracking.
 */
@Service
public class SavingsGoalService {
    
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new savings goal linked to a wallet.
     */
    @Transactional
    public SavingsGoalDTO createSavingsGoal(SavingsGoalDTO goalDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        Wallet wallet = walletRepository.findById(goalDTO.getWalletId())
                .orElseThrow(() -> new RuntimeException("Novčanik ne postoji"));
        
        if (!wallet.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom novčaniku");
        }
        
        // Mark wallet as savings wallet if not already
        if (!wallet.isSavings()) {
            wallet.setSavings(true);
            walletRepository.save(wallet);
        }
        
        SavingsGoal goal = new SavingsGoal();
        goal.setName(goalDTO.getName());
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setTargetDate(goalDTO.getTargetDate());
        goal.setWallet(wallet);
        goal.setUser(user);
        
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);
        return convertToDTO(savedGoal);
    }
    
    /**
     * Get all savings goals for a user.
     */
    public List<SavingsGoalDTO> getUserSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get savings goal by ID.
     */
    public SavingsGoalDTO getSavingsGoalById(Long goalId, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Cilj štednje ne postoji"));
        
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom cilju");
        }
        
        return convertToDTO(goal);
    }
    
    /**
     * Update savings goal progress based on wallet balance.
     */
    @Transactional
    public SavingsGoalDTO updateProgress(Long goalId, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Cilj štednje ne postoji"));
        
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom cilju");
        }
        
        // Update current amount based on wallet balance
        goal.setCurrentAmount(goal.getWallet().getCurrentBalance());
        
        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }
    
    /**
     * Update savings goal details.
     */
    @Transactional
    public SavingsGoalDTO updateSavingsGoal(Long goalId, SavingsGoalDTO goalDTO, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Cilj štednje ne postoji"));
        
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom cilju");
        }
        
        goal.setName(goalDTO.getName());
        goal.setTargetAmount(goalDTO.getTargetAmount());
        goal.setTargetDate(goalDTO.getTargetDate());
        
        SavingsGoal updatedGoal = savingsGoalRepository.save(goal);
        return convertToDTO(updatedGoal);
    }
    
    /**
     * Delete savings goal.
     */
    @Transactional
    public void deleteSavingsGoal(Long goalId, Long userId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
                .orElseThrow(() -> new RuntimeException("Cilj štednje ne postoji"));
        
        if (!goal.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovom cilju");
        }
        
        savingsGoalRepository.delete(goal);
    }
    
    /**
     * Convert SavingsGoal entity to SavingsGoalDTO with calculated fields.
     */
    private SavingsGoalDTO convertToDTO(SavingsGoal goal) {
        SavingsGoalDTO dto = new SavingsGoalDTO(
            goal.getId(),
            goal.getName(),
            goal.getTargetAmount(),
            goal.getCurrentAmount(),
            goal.getTargetDate(),
            goal.getWallet().getId(),
            goal.getWallet().getName(),
            goal.getDateOfCreation(),
            goal.getUser().getId(),
            goal.isCompleted()
        );
        
        // Add calculated fields
        dto.setProgressPercentage(goal.getProgressPercentage());
        dto.setRemainingAmount(goal.getRemainingAmount());
        dto.setOverdue(goal.isOverdue());
        dto.setOnTrack(goal.isOnTrack());
        
        return dto;
    }
}
