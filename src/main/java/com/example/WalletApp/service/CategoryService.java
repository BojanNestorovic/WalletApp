package com.example.WalletApp.service;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CategoryService {
    
    // CRUD Operations
    Category createCategory(Category category);
    Category updateCategory(Long id, Category category);
    void deleteCategory(Long id);
    Optional<Category> getCategoryById(Long id);
    List<Category> getAllCategories();
    Page<Category> getAllCategories(Pageable pageable);
    
    // User-specific operations
    List<Category> getCategoriesByUserId(Long userId);
    List<Category> getUserDefinedCategories(Long userId);
    List<Category> getPredefinedCategories();
    
    // Category filtering
    List<Category> getCategoriesByType(String type);
    List<Category> getCategoriesByTypeAndUser(String type, Long userId);
    
    // DTO Conversion
    CategoryDTO convertToDTO(Category category);
    Category convertToEntity(CategoryDTO categoryDTO);
}
