package com.example.WalletApp.repository;

import com.example.WalletApp.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    List<Wallet> findByUserId(Long userId);
    
    List<Wallet> findByUserIdAndArchived(Long userId, boolean archived);
    
    List<Wallet> findByUserIdAndSavings(Long userId, boolean savings);
    
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId AND w.archived = false")
    List<Wallet> findActiveWalletsByUserId(@Param("userId") Long userId);
    
    @Query("SELECT SUM(w.currentBalance) FROM Wallet w WHERE w.user.id = :userId AND w.archived = false")
    BigDecimal getTotalBalanceByUserId(@Param("userId") Long userId);
    
    @Query("SELECT AVG(w.currentBalance) FROM Wallet w WHERE w.archived = false")
    BigDecimal getAverageWalletBalance();
    
    @Query("SELECT SUM(w.currentBalance) FROM Wallet w WHERE w.archived = false")
    BigDecimal getTotalSystemBalance();
    
    @Query("SELECT w FROM Wallet w WHERE w.currency.id = :currencyId")
    List<Wallet> findByCurrencyId(@Param("currencyId") Long currencyId);
}
