package com.example.WalletApp.repository;

import com.example.WalletApp.entity.Category;
import com.example.WalletApp.entity.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByPredefined(boolean predefined);
    
    List<Category> findByUserId(Long userId);
    
    List<Category> findByType(String type);
    
    List<Category> findByUserIdAndType(Long userId, String type);
    
    @Query("SELECT c FROM Category c WHERE c.predefined = true OR c.user.id = :userId")
    List<Category> findAvailableCategoriesForUser(@Param("userId") Long userId);
    
    @Query("SELECT c FROM Category c WHERE c.predefined = true")
    List<Category> findPredefinedCategories();
    
    @Query("SELECT c FROM Category c WHERE c.user.id = :userId AND c.predefined = false")
    List<Category> findUserCreatedCategories(@Param("userId") Long userId);
    
    boolean existsByNameAndUserId(String name, Long userId);
    
    boolean existsByNameAndPredefined(String name, boolean predefined);
    
    // Additional methods needed by service
    List<Category> findByUserIdAndPredefined(Long userId, boolean predefined);
    
    List<Category> findByType(CategoryType type);
    
    List<Category> findByTypeAndUserId(CategoryType type, Long userId);
}
