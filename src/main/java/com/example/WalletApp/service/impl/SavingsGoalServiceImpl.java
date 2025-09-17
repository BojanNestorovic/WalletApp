package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.SavingsGoalDTO;
import com.example.WalletApp.entity.SavingsGoal;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.repository.SavingsGoalRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.repository.WalletRepository;
import com.example.WalletApp.service.SavingsGoalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SavingsGoalServiceImpl implements SavingsGoalService {
    
    @Autowired
    private SavingsGoalRepository savingsGoalRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Override
    public SavingsGoal createSavingsGoal(SavingsGoal savingsGoal) {
        return savingsGoalRepository.save(savingsGoal);
    }
    
    @Override
    public SavingsGoal updateSavingsGoal(Long id, SavingsGoal savingsGoal) {
        SavingsGoal existingGoal = savingsGoalRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + id));
        
        existingGoal.setName(savingsGoal.getName());
        existingGoal.setTargetAmount(savingsGoal.getTargetAmount());
        existingGoal.setTargetDate(savingsGoal.getTargetDate());
        
        return savingsGoalRepository.save(existingGoal);
    }
    
    @Override
    public void deleteSavingsGoal(Long id) {
        if (!savingsGoalRepository.existsById(id)) {
            throw new RuntimeException("Savings goal not found with id: " + id);
        }
        savingsGoalRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<SavingsGoal> getSavingsGoalById(Long id) {
        return savingsGoalRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getAllSavingsGoals() {
        return savingsGoalRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<SavingsGoal> getAllSavingsGoals(Pageable pageable) {
        return savingsGoalRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getSavingsGoalsByUserId(Long userId) {
        return savingsGoalRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getSavingsGoalsByWalletId(Long walletId) {
        return savingsGoalRepository.findByWalletId(walletId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getCompletedSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserIdAndCompleted(userId, true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getActiveSavingsGoals(Long userId) {
        return savingsGoalRepository.findByUserIdAndCompleted(userId, false);
    }
    
    @Override
    public void addToSavingsGoal(Long goalId, BigDecimal amount) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + goalId));
        
        goal.addAmount(amount);
        savingsGoalRepository.save(goal);
    }
    
    @Override
    public void completeSavingsGoal(Long goalId) {
        SavingsGoal goal = savingsGoalRepository.findById(goalId)
            .orElseThrow(() -> new RuntimeException("Savings goal not found with id: " + goalId));
        
        goal.setCompleted(true);
        savingsGoalRepository.save(goal);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getOverdueGoals(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
            .filter(SavingsGoal::isOverdue)
            .toList();
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<SavingsGoal> getGoalsByDateRange(Long userId, Date startDate, Date endDate) {
        return savingsGoalRepository.findByUserIdAndTargetDateBetween(userId, startDate, endDate);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalSavingsByUserId(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
            .map(SavingsGoal::getCurrentAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalTargetAmountByUserId(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
            .map(SavingsGoal::getTargetAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public SavingsGoalDTO convertToDTO(SavingsGoal savingsGoal) {
        SavingsGoalDTO dto = new SavingsGoalDTO();
        dto.setId(savingsGoal.getId());
        dto.setName(savingsGoal.getName());
        dto.setTargetAmount(savingsGoal.getTargetAmount());
        dto.setCurrentAmount(savingsGoal.getCurrentAmount());
        dto.setTargetDate(savingsGoal.getTargetDate());
        dto.setDateOfCreation(savingsGoal.getDateOfCreation());
        dto.setCompleted(savingsGoal.isCompleted());
        
        if (savingsGoal.getWallet() != null) {
            dto.setWalletId(savingsGoal.getWallet().getId());
        }
        
        if (savingsGoal.getUser() != null) {
            dto.setUserId(savingsGoal.getUser().getId());
        }
        
        return dto;
    }
    
    @Override
    public SavingsGoal convertToEntity(SavingsGoalDTO dto) {
        SavingsGoal savingsGoal = new SavingsGoal();
        savingsGoal.setId(dto.getId());
        savingsGoal.setName(dto.getName());
        savingsGoal.setTargetAmount(dto.getTargetAmount());
        // Set currentAmount to zero if not provided
        if (dto.getCurrentAmount() != null) {
            savingsGoal.setCurrentAmount(dto.getCurrentAmount());
        } else {
            savingsGoal.setCurrentAmount(BigDecimal.ZERO);
        }
        savingsGoal.setTargetDate(dto.getTargetDate());
        // Set dateOfCreation to current date if not provided
        if (dto.getDateOfCreation() != null) {
            savingsGoal.setDateOfCreation(dto.getDateOfCreation());
        } else {
            savingsGoal.setDateOfCreation(new Date());
        }
        savingsGoal.setCompleted(dto.isCompleted());
        
        if (dto.getWalletId() != null) {
            Wallet wallet = walletRepository.findById(dto.getWalletId()).orElse(null);
            savingsGoal.setWallet(wallet);
        }
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            savingsGoal.setUser(user);
        }
        
        return savingsGoal;
    }
}
