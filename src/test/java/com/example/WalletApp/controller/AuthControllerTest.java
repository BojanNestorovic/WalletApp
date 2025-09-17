package com.example.WalletApp.controller;

import com.example.WalletApp.config.TestSecurityConfig;
import com.example.WalletApp.dto.LoginDTO;
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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO testUserDTO;
    private LoginDTO testLoginDTO;

    @BeforeEach
    void setUp() {
        testUserDTO = new UserDTO();
        testUserDTO.setFirstName("John");
        testUserDTO.setLastName("Doe");
        testUserDTO.setUsername("johndoe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setPassword("password123");
        testUserDTO.setBirthDate(new Date());
        testUserDTO.setRole("USER");

        testLoginDTO = new LoginDTO();
        testLoginDTO.setUsername("johndoe");
        testLoginDTO.setPassword("password123");
    }

    @Test
    void testRegisterUser_Success() throws Exception {
        // Given
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("johndoe");
        
        when(userService.convertToEntity(any(UserDTO.class))).thenReturn(mockUser);
        when(userService.registerUser(any(User.class))).thenReturn(mockUser);
        when(userService.convertToDTO(any(User.class))).thenReturn(testUserDTO);

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"));
    }

    @Test
    void testRegisterUser_ValidationError() throws Exception {
        // Given
        testUserDTO.setUsername(""); // Invalid username

        // When & Then
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        when(userService.validateUserCredentials("johndoe", "password123")).thenReturn(true);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        when(userService.validateUserCredentials("johndoe", "password123")).thenReturn(false);

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid credentials"));
    }

    @Test
    void testLogin_ValidationError() throws Exception {
        // Given
        testLoginDTO.setUsername(""); // Invalid username

        // When & Then
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testLoginDTO)))
                .andExpect(status().isBadRequest());
    }
}
