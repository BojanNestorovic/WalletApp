package com.example.WalletApp.integration;

import com.example.WalletApp.config.TestSecurityConfig;
import com.example.WalletApp.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.example.WalletApp.DemoAplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(TestSecurityConfig.class)
class WalletAppIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCompleteUserWorkflow() throws Exception {
        // 1. Create a currency
        CurrencyDTO currencyDTO = new CurrencyDTO();
        currencyDTO.setName("USD");
        currencyDTO.setValueToEur(1.0);

        MvcResult currencyResult = mockMvc.perform(post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currencyDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("USD"))
                .andReturn();

        String currencyResponse = currencyResult.getResponse().getContentAsString();
        CurrencyDTO createdCurrency = objectMapper.readValue(currencyResponse, CurrencyDTO.class);

        // 2. Register a user
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Doe");
        userDTO.setUsername("johndoe");
        userDTO.setEmail("john@example.com");
        userDTO.setPassword("password123");
        userDTO.setBirthDate(new Date());
        userDTO.setRole("USER");

        MvcResult userResult = mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("johndoe"))
                .andReturn();

        String userResponse = userResult.getResponse().getContentAsString();
        UserDTO createdUser = objectMapper.readValue(userResponse, UserDTO.class);

        // 3. Create a wallet
        WalletDTO walletDTO = new WalletDTO();
        walletDTO.setName("Main Wallet");
        walletDTO.setInitialBalance(new BigDecimal("1000.00"));
        walletDTO.setUserId(createdUser.getId());
        walletDTO.setCurrencyId(createdCurrency.getId());

        MvcResult walletResult = mockMvc.perform(post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(walletDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Main Wallet"))
                .andReturn();

        String walletResponse = walletResult.getResponse().getContentAsString();
        WalletDTO createdWallet = objectMapper.readValue(walletResponse, WalletDTO.class);

        // 4. Create a category
        CategoryDTO categoryDTO = new CategoryDTO();
        categoryDTO.setName("Food");
        categoryDTO.setType("EXPENSE");
        categoryDTO.setPredefined(false);
        categoryDTO.setUserId(createdUser.getId());

        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Food"))
                .andReturn();

        String categoryResponse = categoryResult.getResponse().getContentAsString();
        CategoryDTO createdCategory = objectMapper.readValue(categoryResponse, CategoryDTO.class);

        // 5. Create a transaction
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setName("Grocery Shopping");
        transactionDTO.setAmount(new BigDecimal("50.00"));
        transactionDTO.setType("EXPENSE");
        transactionDTO.setCategoryId(createdCategory.getId());
        transactionDTO.setWalletId(createdWallet.getId());
        transactionDTO.setUserId(createdUser.getId());
        transactionDTO.setDateOfTransaction(new Date());

        mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Grocery Shopping"));

        // 6. Create a savings goal
        SavingsGoalDTO savingsGoalDTO = new SavingsGoalDTO();
        savingsGoalDTO.setName("Vacation Fund");
        savingsGoalDTO.setTargetAmount(new BigDecimal("2000.00"));
        savingsGoalDTO.setWalletId(createdWallet.getId());
        savingsGoalDTO.setUserId(createdUser.getId());
        savingsGoalDTO.setTargetDate(new Date(System.currentTimeMillis() + 86400000 * 30)); // 30 days from now

        mockMvc.perform(post("/api/savings-goals")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(savingsGoalDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Vacation Fund"));

        // 7. Verify user can login
        LoginDTO loginDTO = new LoginDTO();
        loginDTO.setUsername("johndoe");
        loginDTO.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Login successful"));

        // 8. Get user's wallets
        mockMvc.perform(get("/api/wallets/user/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Main Wallet"));

        // 9. Get user's transactions
        mockMvc.perform(get("/api/transactions/user/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Grocery Shopping"));

        // 10. Get user's categories
        mockMvc.perform(get("/api/categories/user/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Food"));

        // 11. Get user's savings goals
        mockMvc.perform(get("/api/savings-goals/user/" + createdUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Vacation Fund"));

        // 12. Get transaction statistics
        mockMvc.perform(get("/api/transactions/user/" + createdUser.getId() + "/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total Income: 0.00, Total Expense: 50.00, Net Balance: -50.00"));

        // 13. Get wallet statistics
        mockMvc.perform(get("/api/wallets/stats"))
                .andExpect(status().isOk());
    }

    @Test
    void testUserManagementWorkflow() throws Exception {
        // 1. Register multiple users
        UserDTO user1DTO = new UserDTO();
        user1DTO.setFirstName("Alice");
        user1DTO.setLastName("Smith");
        user1DTO.setUsername("alice");
        user1DTO.setEmail("alice@example.com");
        user1DTO.setPassword("password123");
        user1DTO.setBirthDate(new Date());
        user1DTO.setRole("USER");

        UserDTO user2DTO = new UserDTO();
        user2DTO.setFirstName("Bob");
        user2DTO.setLastName("Johnson");
        user2DTO.setUsername("bob");
        user2DTO.setEmail("bob@example.com");
        user2DTO.setPassword("password123");
        user2DTO.setBirthDate(new Date());
        user2DTO.setRole("ADMINISTRATOR");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1DTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2DTO)))
                .andExpect(status().isCreated());

        // 2. Get all users
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        // 3. Get users by role
        mockMvc.perform(get("/api/users/role/USER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("USER"));

        mockMvc.perform(get("/api/users/role/ADMINISTRATOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("ADMINISTRATOR"));

        // 4. Get user statistics
        mockMvc.perform(get("/api/users/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total Users: 2, Active Users: 2, Blocked Users: 0"));

        // 5. Search users by name
        mockMvc.perform(get("/api/users/search")
                .param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    void testCurrencyManagementWorkflow() throws Exception {
        // 1. Create multiple currencies
        CurrencyDTO usdDTO = new CurrencyDTO();
        usdDTO.setName("USD");
        usdDTO.setValueToEur(1.0);

        CurrencyDTO eurDTO = new CurrencyDTO();
        eurDTO.setName("EUR");
        eurDTO.setValueToEur(1.0);

        CurrencyDTO rsdDTO = new CurrencyDTO();
        rsdDTO.setName("RSD");
        rsdDTO.setValueToEur(0.01);

        mockMvc.perform(post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(usdDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eurDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/currencies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rsdDTO)))
                .andExpect(status().isCreated());

        // 2. Get all currencies
        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        // 3. Get currency by name
        mockMvc.perform(get("/api/currencies/name/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USD"));

        // 4. Check if currency exists
        mockMvc.perform(get("/api/currencies/exists/USD"))
                .andExpect(status().isOk())
                .andExpect(content().string("Currency exists: true"));

        mockMvc.perform(get("/api/currencies/exists/GBP"))
                .andExpect(status().isOk())
                .andExpect(content().string("Currency exists: false"));
    }
}
