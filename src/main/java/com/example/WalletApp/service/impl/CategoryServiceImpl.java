package com.example.WalletApp.service.impl;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.entity.Category;
import com.example.WalletApp.entity.CategoryType;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CategoryRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Category createCategory(Category category) {
        return categoryRepository.save(category);
    }
    
    @Override
    public Category updateCategory(Long id, Category category) {
        Category existingCategory = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        
        existingCategory.setName(category.getName());
        existingCategory.setType(category.getType());
        
        return categoryRepository.save(existingCategory);
    }
    
    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByUserId(Long userId) {
        return categoryRepository.findByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getUserDefinedCategories(Long userId) {
        return categoryRepository.findByUserIdAndPredefined(userId, false);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getPredefinedCategories() {
        return categoryRepository.findByPredefined(true);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByType(String type) {
        try {
            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
            return categoryRepository.findByType(categoryType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category type: " + type);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoriesByTypeAndUser(String type, Long userId) {
        try {
            CategoryType categoryType = CategoryType.valueOf(type.toUpperCase());
            return categoryRepository.findByTypeAndUserId(categoryType, userId);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid category type: " + type);
        }
    }
    
    @Override
    public CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setType(category.getType() != null ? category.getType().toString() : null);
        dto.setPredefined(category.isPredefined());
        
        if (category.getUser() != null) {
            dto.setUserId(category.getUser().getId());
        }
        
        return dto;
    }
    
    @Override
    public Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setPredefined(dto.isPredefined());
        
        if (dto.getType() != null) {
            try {
                category.setType(CategoryType.valueOf(dto.getType().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid category type: " + dto.getType());
            }
        }
        
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId()).orElse(null);
            category.setUser(user);
        }
        
        return category;
    }
}
