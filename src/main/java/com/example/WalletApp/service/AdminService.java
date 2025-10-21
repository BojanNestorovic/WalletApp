package com.example.WalletApp.service;

import com.example.WalletApp.entity.AdminNote;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Service for Admin-specific operations: monitoring, notes, statistics.
 */
@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminNoteRepository adminNoteRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private WalletRepository walletRepository;
    
    /**
     * Add admin note about a user.
     */
    @Transactional
    public AdminNote addNoteToUser(Long userId, Long adminId, String noteText) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin ne postoji"));
        
        AdminNote note = new AdminNote(user, admin, noteText);
        return adminNoteRepository.save(note);
    }
    
    /**
     * Get all notes for a user.
     */
    public List<AdminNote> getUserNotes(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        return adminNoteRepository.findByUserOrderByDateCreatedDesc(user);
    }
    
    /**
     * Get dashboard metrics for admin.
     */
    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // Total users
        long totalUsers = userRepository.count();
        metrics.put("totalUsers", totalUsers);
        
        // Active users (not blocked)
        long activeUsers = userRepository.countActiveUsers();
        metrics.put("activeUsers", activeUsers);
        
        // Total system balance
        BigDecimal totalBalance = walletRepository.getTotalSystemBalance();
        metrics.put("totalSystemBalance", totalBalance != null ? totalBalance : BigDecimal.ZERO);
        
        // Average wallet balance
        BigDecimal avgBalance = walletRepository.getAverageWalletBalance();
        metrics.put("averageWalletBalance", avgBalance != null ? avgBalance : BigDecimal.ZERO);
        
        // Top 10 transactions in last 30 days
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();
        
        List<?> top10Last30Days = transactionRepository.findTop10TransactionsByDate(thirtyDaysAgo)
                .stream()
                .limit(10)
                .map(t -> {
                    Map<String, Object> txMap = new HashMap<>();
                    txMap.put("id", t.getId());
                    txMap.put("name", t.getName());
                    txMap.put("amount", t.getAmount());
                    txMap.put("type", t.getType().toString());
                    txMap.put("date", t.getDateOfTransaction());
                    txMap.put("user", t.getUser().getUsername());
                    return txMap;
                })
                .toList();
        metrics.put("top10Transactions30Days", top10Last30Days);
        
        // Top 10 transactions in last 2 minutes (for testing)
        Calendar cal2 = Calendar.getInstance();
        cal2.add(Calendar.MINUTE, -2);
        Date twoMinutesAgo = cal2.getTime();
        
        List<?> top10Last2Minutes = transactionRepository.findTop10TransactionsByDate(twoMinutesAgo)
                .stream()
                .limit(10)
                .map(t -> {
                    Map<String, Object> txMap = new HashMap<>();
                    txMap.put("id", t.getId());
                    txMap.put("name", t.getName());
                    txMap.put("amount", t.getAmount());
                    txMap.put("type", t.getType().toString());
                    txMap.put("date", t.getDateOfTransaction());
                    txMap.put("user", t.getUser().getUsername());
                    return txMap;
                })
                .toList();
        metrics.put("top10Transactions2Minutes", top10Last2Minutes);
        
        return metrics;
    }
    
    /**
     * Get users registered in last month (active users metric).
     */
    public long getUsersActiveInLastMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        Date oneMonthAgo = cal.getTime();
        
        return userRepository.findUsersRegisteredAfter(oneMonthAgo).size();
    }
}
