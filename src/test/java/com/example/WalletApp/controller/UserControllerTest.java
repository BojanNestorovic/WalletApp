package com.example.WalletApp.controller;

import com.example.WalletApp.config.TestSecurityConfig;
import com.example.WalletApp.dto.UserDTO;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setId(1L);
        testUserDTO.setFirstName("John");
        testUserDTO.setLastName("Doe");
        testUserDTO.setUsername("johndoe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setPassword("password123");
        testUserDTO.setBirthDate(new Date());
        testUserDTO.setRole("USER");
        testUserDTO.setBlocked(false);

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
    }

    @Test
    void testGetAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getAllUsers()).thenReturn(users);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("johndoe"));
    }

    @Test
    void testGetAllUsersWithPagination_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(0, 10), 1);
        when(userService.getAllUsers(any())).thenReturn(userPage);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].username").value("johndoe"));
    }

    @Test
    void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void testGetUserById_NotFound() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateUser_Success() throws Exception {
        // Given
        when(userService.convertToEntity(any(UserDTO.class))).thenReturn(testUser);
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(testUser);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void testUpdateUser_ValidationError() throws Exception {
        // Given
        testUserDTO.setUsername(""); // Invalid username

        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    void testGetUsersByRole_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getUsersByRole("USER")).thenReturn(users);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].username").value("johndoe"));
    }

    @Test
    void testGetBlockedUsers_Success() throws Exception {
        // Given
        testUserDTO.setBlocked(true);
        List<User> users = Arrays.asList(testUser);
        when(userService.getBlockedUsers()).thenReturn(users);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/blocked"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].blocked").value(true));
    }

    @Test
    void testGetActiveUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.getActiveUsers()).thenReturn(users);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].blocked").value(false));
    }

    @Test
    void testGetUserStats_Success() throws Exception {
        // Given
        when(userService.getTotalUserCount()).thenReturn(10L);
        when(userService.getActiveUserCount()).thenReturn(8L);

        // When & Then
        mockMvc.perform(get("/api/users/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total Users: 10, Active Users: 8, Blocked Users: 2"));
    }

    @Test
    void testSearchUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(testUser);
        when(userService.searchUsersByName("John")).thenReturn(users);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(get("/api/users/search")
                .param("name", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("John"));
    }

    @Test
    void testBlockUser_Success() throws Exception {
        // Given
        doNothing().when(userService).blockUser(1L);

        // When & Then
        mockMvc.perform(post("/api/users/1/block"))
                .andExpect(status().isOk())
                .andExpect(content().string("User blocked successfully"));
    }

    @Test
    void testUnblockUser_Success() throws Exception {
        // Given
        doNothing().when(userService).unblockUser(1L);

        // When & Then
        mockMvc.perform(post("/api/users/1/unblock"))
                .andExpect(status().isOk())
                .andExpect(content().string("User unblocked successfully"));
    }

    @Test
    void testUpdateUserCurrency_Success() throws Exception {
        // Given
        doNothing().when(userService).updateUserCurrency(1L, 1L);

        // When & Then
        mockMvc.perform(put("/api/users/1/currency")
                .param("currencyId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("User currency updated successfully"));
    }
}
