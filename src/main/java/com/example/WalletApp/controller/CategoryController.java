package com.example.WalletApp.controller;

import com.example.WalletApp.dto.CategoryDTO;
import com.example.WalletApp.entity.Category;
import com.example.WalletApp.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // Get all categories
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get all categories with pagination
    @GetMapping("/page")
    public ResponseEntity<Page<CategoryDTO>> getAllCategories(Pageable pageable) {
        Page<Category> categories = categoryService.getAllCategories(pageable);
        Page<CategoryDTO> categoryDTOs = categories.map(categoryService::convertToDTO);
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get category by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            CategoryDTO categoryDTO = categoryService.convertToDTO(category.get());
            return ResponseEntity.ok(categoryDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Create new category
    @PostMapping
    public ResponseEntity<?> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = categoryService.convertToEntity(categoryDTO);
            Category createdCategory = categoryService.createCategory(category);
            CategoryDTO responseDTO = categoryService.convertToDTO(createdCategory);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error creating category: " + e.getMessage());
        }
    }

    // Update category
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        try {
            Category category = categoryService.convertToEntity(categoryDTO);
            Category updatedCategory = categoryService.updateCategory(id, category);
            CategoryDTO responseDTO = categoryService.convertToDTO(updatedCategory);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating category: " + e.getMessage());
        }
    }

    // Delete category
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id) {
        try {
            categoryService.deleteCategory(id);
            return ResponseEntity.ok().body("Category deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error deleting category: " + e.getMessage());
        }
    }

    // Get categories by user ID
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByUserId(@PathVariable Long userId) {
        List<Category> categories = categoryService.getCategoriesByUserId(userId);
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get user-defined categories
    @GetMapping("/user/{userId}/custom")
    public ResponseEntity<List<CategoryDTO>> getUserDefinedCategories(@PathVariable Long userId) {
        List<Category> categories = categoryService.getUserDefinedCategories(userId);
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get predefined categories
    @GetMapping("/predefined")
    public ResponseEntity<List<CategoryDTO>> getPredefinedCategories() {
        List<Category> categories = categoryService.getPredefinedCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get categories by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type) {
        List<Category> categories = categoryService.getCategoriesByType(type);
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }

    // Get categories by type and user
    @GetMapping("/type/{type}/user/{userId}")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByTypeAndUser(@PathVariable String type, @PathVariable Long userId) {
        List<Category> categories = categoryService.getCategoriesByTypeAndUser(type, userId);
        List<CategoryDTO> categoryDTOs = categories.stream()
            .map(categoryService::convertToDTO)
            .toList();
        return ResponseEntity.ok(categoryDTOs);
    }
}
