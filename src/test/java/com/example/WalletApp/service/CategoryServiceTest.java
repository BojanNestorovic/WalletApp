package com.example.WalletApp.service;

import com.example.WalletApp.entity.Category;
import com.example.WalletApp.entity.CategoryType;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CategoryRepository;
import com.example.WalletApp.repository.UserRepository;
import com.example.WalletApp.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    private Category testCategory;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food");
        testCategory.setType(CategoryType.EXPENSE);
        testCategory.setPredefined(false);
        testCategory.setUser(testUser);
    }

    @Test
    void testCreateCategory_Success() {
        // Given
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        Category result = categoryService.createCategory(testCategory);

        // Then
        assertNotNull(result);
        assertEquals("Food", result.getName());
        assertEquals(CategoryType.EXPENSE, result.getType());
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void testUpdateCategory_Success() {
        // Given
        Category updatedCategory = new Category();
        updatedCategory.setName("Updated Food");
        updatedCategory.setType(CategoryType.INCOME);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);

        // When
        Category result = categoryService.updateCategory(1L, updatedCategory);

        // Then
        assertNotNull(result);
        verify(categoryRepository).save(testCategory);
    }

    @Test
    void testUpdateCategory_CategoryNotFound() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(1L, testCategory);
        });

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void testDeleteCategory_Success() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(true);

        // When
        categoryService.deleteCategory(1L);

        // Then
        verify(categoryRepository).deleteById(1L);
    }

    @Test
    void testDeleteCategory_CategoryNotFound() {
        // Given
        when(categoryRepository.existsById(1L)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(1L);
        });

        assertEquals("Category not found with id: 1", exception.getMessage());
        verify(categoryRepository, never()).deleteById(anyLong());
    }

    @Test
    void testGetCategoryById_Found() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // When
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testCategory, result.get());
    }

    @Test
    void testGetCategoryById_NotFound() {
        // Given
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        // When
        Optional<Category> result = categoryService.getCategoryById(1L);

        // Then
        assertFalse(result.isPresent());
    }

    @Test
    void testGetCategoriesByUserId() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUserId(1L)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByUserId(1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));
    }

    @Test
    void testGetUserDefinedCategories() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByUserIdAndPredefined(1L, false)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getUserDefinedCategories(1L);

        // Then
        assertEquals(1, result.size());
        assertFalse(result.get(0).isPredefined());
    }

    @Test
    void testGetPredefinedCategories() {
        // Given
        testCategory.setPredefined(true);
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByPredefined(true)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getPredefinedCategories();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).isPredefined());
    }

    @Test
    void testGetCategoriesByType_Success() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByType(CategoryType.EXPENSE)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByType("EXPENSE");

        // Then
        assertEquals(1, result.size());
        assertEquals(CategoryType.EXPENSE, result.get(0).getType());
    }

    @Test
    void testGetCategoriesByType_InvalidType() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoriesByType("INVALID");
        });

        assertEquals("Invalid category type: INVALID", exception.getMessage());
    }

    @Test
    void testGetCategoriesByTypeAndUser_Success() {
        // Given
        List<Category> categories = Arrays.asList(testCategory);
        when(categoryRepository.findByTypeAndUserId(CategoryType.EXPENSE, 1L)).thenReturn(categories);

        // When
        List<Category> result = categoryService.getCategoriesByTypeAndUser("EXPENSE", 1L);

        // Then
        assertEquals(1, result.size());
        assertEquals(CategoryType.EXPENSE, result.get(0).getType());
    }

    @Test
    void testGetCategoriesByTypeAndUser_InvalidType() {
        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            categoryService.getCategoriesByTypeAndUser("INVALID", 1L);
        });

        assertEquals("Invalid category type: INVALID", exception.getMessage());
    }
}
