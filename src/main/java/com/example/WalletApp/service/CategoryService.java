package com.example.WalletApp.service;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.entity.Category;
import com.example.WalletApp.entity.CategoryType;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CategoryRepository;
import com.example.WalletApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Category management.
 */
@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all predefined categories.
     */
    public List<CategoryDTO> getPredefinedCategories() {
        return categoryRepository.findByPredefined(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all categories (admin only).
     */
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get user's custom categories.
     */
    public List<CategoryDTO> getUserCategories(Long userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get all categories available to user (predefined + user's custom).
     */
    public List<CategoryDTO> getAllAvailableCategories(Long userId) {
        List<CategoryDTO> categories = getPredefinedCategories();
        categories.addAll(getUserCategories(userId));
        return categories;
    }
    
    /**
     * Create custom category for user.
     */
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Korisnik ne postoji"));
        
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setType(CategoryType.valueOf(categoryDTO.getType()));
        category.setPredefined(false);
        category.setUser(user);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }
    
    /**
     * Create predefined category (admin only).
     */
    @Transactional
    public CategoryDTO createPredefinedCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setType(CategoryType.valueOf(categoryDTO.getType()));
        category.setPredefined(true);
        category.setUser(null);
        
        Category savedCategory = categoryRepository.save(category);
        return convertToDTO(savedCategory);
    }
    
    /**
     * Delete user's custom category.
     */
    @Transactional
    public void deleteCategory(Long categoryId, Long userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategorija ne postoji"));
        
        if (category.isPredefined()) {
            throw new RuntimeException("Ne možete obrisati predefinisanu kategoriju");
        }
        
        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Nemate pristup ovoj kategoriji");
        }
        
        categoryRepository.delete(category);
    }
    
    /**
     * Delete predefined category (admin only).
     */
    @Transactional
    public void deletePredefinedCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Kategorija ne postoji"));
        
        if (!category.isPredefined()) {
            throw new RuntimeException("Ovo nije predefinisana kategorija");
        }
        
        categoryRepository.delete(category);
    }
    
    /**
     * Convert Category entity to CategoryDTO.
     */
    private CategoryDTO convertToDTO(Category category) {
        return new CategoryDTO(
            category.getId(),
            category.getName(),
            category.getType().toString(),
            category.isPredefined(),
            category.getUser() != null ? category.getUser().getId() : null
        );
    }
}
