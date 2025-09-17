package com.example.WalletApp.repository;

import com.example.WalletApp.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {
    
    List<SavingsGoal> findByUserId(Long userId);
    
    List<SavingsGoal> findByUserIdAndCompleted(Long userId, boolean completed);
    
    List<SavingsGoal> findByWalletId(Long walletId);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.targetDate <= :date AND sg.completed = false")
    List<SavingsGoal> findOverdueGoalsByUserId(@Param("userId") Long userId, @Param("date") Date date);
    
    @Query("SELECT sg FROM SavingsGoal sg WHERE sg.user.id = :userId AND sg.targetDate BETWEEN :startDate AND :endDate")
    List<SavingsGoal> findGoalsByUserIdAndDateRange(@Param("userId") Long userId, 
                                                   @Param("startDate") Date startDate, 
                                                   @Param("endDate") Date endDate);
    
    // Additional method needed by service
    List<SavingsGoal> findByUserIdAndTargetDateBetween(Long userId, Date startDate, Date endDate);
}
