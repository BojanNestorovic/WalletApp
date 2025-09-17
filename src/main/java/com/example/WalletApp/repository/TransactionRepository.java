package com.example.WalletApp.repository;

import com.example.WalletApp.entity.Transaction;
import com.example.WalletApp.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    List<Transaction> findByUserId(Long userId);
    
    List<Transaction> findByWalletId(Long walletId);
    
    List<Transaction> findByCategoryId(Long categoryId);
    
    List<Transaction> findByUserIdAndType(Long userId, String type);
    
    List<Transaction> findByUserIdAndRepeating(Long userId, boolean repeating);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.dateOfTransaction BETWEEN :startDate AND :endDate")
    List<Transaction> findByUserIdAndDateRange(@Param("userId") Long userId, 
                                             @Param("startDate") Date startDate, 
                                             @Param("endDate") Date endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> findByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);
    
    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.amount >= :minAmount AND t.amount <= :maxAmount")
    List<Transaction> findByUserIdAndAmountRange(@Param("userId") Long userId, 
                                               @Param("minAmount") BigDecimal minAmount, 
                                               @Param("maxAmount") BigDecimal maxAmount);
    
    @Query("SELECT t FROM Transaction t WHERE t.dateOfTransaction >= :date ORDER BY t.amount DESC")
    Page<Transaction> findTopTransactionsByDate(@Param("date") Date date, Pageable pageable);
    
    @Query("SELECT t FROM Transaction t WHERE t.dateOfTransaction >= :date ORDER BY t.amount DESC")
    List<Transaction> findTop10TransactionsByDate(@Param("date") Date date);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'INCOME' AND t.dateOfTransaction BETWEEN :startDate AND :endDate")
    BigDecimal getTotalIncomeByUserIdAndDateRange(@Param("userId") Long userId, 
                                                @Param("startDate") Date startDate, 
                                                @Param("endDate") Date endDate);
    
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.dateOfTransaction BETWEEN :startDate AND :endDate")
    BigDecimal getTotalExpensesByUserIdAndDateRange(@Param("userId") Long userId, 
                                                  @Param("startDate") Date startDate, 
                                                  @Param("endDate") Date endDate);
    
    @Query("SELECT t.category.id, SUM(t.amount) FROM Transaction t WHERE t.user.id = :userId AND t.type = 'EXPENSE' AND t.dateOfTransaction BETWEEN :startDate AND :endDate GROUP BY t.category.id ORDER BY SUM(t.amount) DESC")
    List<Object[]> getExpensesByCategoryAndDateRange(@Param("userId") Long userId, 
                                                   @Param("startDate") Date startDate, 
                                                   @Param("endDate") Date endDate);
    
    @Query("SELECT t FROM Transaction t WHERE t.repeating = true AND t.dateOfTransaction <= :date")
    List<Transaction> findRepeatingTransactionsDue(@Param("date") Date date);
    
    // Additional methods needed by service
    List<Transaction> findByType(TransactionType type);
    
    List<Transaction> findByRepeating(boolean repeating);
    
    List<Transaction> findByDateOfTransactionBetween(Date startDate, Date endDate);
    
    List<Transaction> findByUserIdAndDateOfTransactionBetween(Long userId, Date startDate, Date endDate);
}
