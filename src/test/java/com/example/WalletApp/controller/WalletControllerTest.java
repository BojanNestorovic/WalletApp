package com.example.WalletApp.controller;

import com.example.WalletApp.config.TestSecurityConfig;
import com.example.WalletApp.dto.WalletDTO;
import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.entity.Wallet;
import com.example.WalletApp.service.WalletService;
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

import java.math.BigDecimal;
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

@WebMvcTest(WalletController.class)
@Import(TestSecurityConfig.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    private WalletDTO testWalletDTO;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testWalletDTO = new WalletDTO();
        testWalletDTO.setId(1L);
        testWalletDTO.setName("Test Wallet");
        testWalletDTO.setInitialBalance(new BigDecimal("1000.00"));
        testWalletDTO.setCurrentBalance(new BigDecimal("1000.00"));
        testWalletDTO.setUserId(1L);
        testWalletDTO.setCurrencyId(1L);
        testWalletDTO.setSavings(false);
        testWalletDTO.setArchived(false);

        testWallet = new Wallet();
        testWallet.setId(1L);
        testWallet.setName("Test Wallet");
        testWallet.setInitialBalance(new BigDecimal("1000.00"));
        testWallet.setCurrentBalance(new BigDecimal("1000.00"));
    }

    @Test
    void testGetAllWallets_Success() throws Exception {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletService.getAllWallets()).thenReturn(wallets);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Wallet"));
    }

    @Test
    void testGetAllWalletsWithPagination_Success() throws Exception {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        Page<Wallet> walletPage = new PageImpl<>(wallets, PageRequest.of(0, 10), 1);
        when(walletService.getAllWallets(any())).thenReturn(walletPage);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/page"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("Test Wallet"));
    }

    @Test
    void testGetWalletById_Success() throws Exception {
        // Given
        when(walletService.getWalletById(1L)).thenReturn(Optional.of(testWallet));
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Wallet"));
    }

    @Test
    void testGetWalletById_NotFound() throws Exception {
        // Given
        when(walletService.getWalletById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/wallets/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateWallet_Success() throws Exception {
        // Given
        when(walletService.convertToEntity(any(WalletDTO.class))).thenReturn(testWallet);
        when(walletService.createWallet(any(Wallet.class))).thenReturn(testWallet);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWalletDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Wallet"));
    }

    @Test
    void testCreateWallet_ValidationError() throws Exception {
        // Given
        testWalletDTO.setName(""); // Invalid name

        // When & Then
        mockMvc.perform(post("/api/wallets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWalletDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateWallet_Success() throws Exception {
        // Given
        when(walletService.convertToEntity(any(WalletDTO.class))).thenReturn(testWallet);
        when(walletService.updateWallet(anyLong(), any(Wallet.class))).thenReturn(testWallet);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(put("/api/wallets/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testWalletDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Wallet"));
    }

    @Test
    void testDeleteWallet_Success() throws Exception {
        // Given
        doNothing().when(walletService).deleteWallet(1L);

        // When & Then
        mockMvc.perform(delete("/api/wallets/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet deleted successfully"));
    }

    @Test
    void testGetWalletsByUserId_Success() throws Exception {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletService.getWalletsByUserId(1L)).thenReturn(wallets);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Wallet"));
    }

    @Test
    void testGetActiveWalletsByUserId_Success() throws Exception {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletService.getActiveWalletsByUserId(1L)).thenReturn(wallets);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/user/1/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Wallet"));
    }

    @Test
    void testGetSavingsWalletsByUserId_Success() throws Exception {
        // Given
        testWalletDTO.setSavings(true);
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletService.getSavingsWalletsByUserId(1L)).thenReturn(wallets);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/user/1/savings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].savings").value(true));
    }

    @Test
    void testGetTotalBalanceByUserId_Success() throws Exception {
        // Given
        BigDecimal totalBalance = new BigDecimal("1500.00");
        when(walletService.getTotalBalanceByUserId(1L)).thenReturn(totalBalance);

        // When & Then
        mockMvc.perform(get("/api/wallets/user/1/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("Total balance: 1500.00"));
    }

    @Test
    void testArchiveWallet_Success() throws Exception {
        // Given
        doNothing().when(walletService).archiveWallet(1L);

        // When & Then
        mockMvc.perform(post("/api/wallets/1/archive"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet archived successfully"));
    }

    @Test
    void testUnarchiveWallet_Success() throws Exception {
        // Given
        doNothing().when(walletService).unarchiveWallet(1L);

        // When & Then
        mockMvc.perform(post("/api/wallets/1/unarchive"))
                .andExpect(status().isOk())
                .andExpect(content().string("Wallet unarchived successfully"));
    }

    @Test
    void testAddToWalletBalance_Success() throws Exception {
        // Given
        doNothing().when(walletService).addToWalletBalance(1L, new BigDecimal("100.00"));

        // When & Then
        mockMvc.perform(post("/api/wallets/1/add")
                .param("amount", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount added successfully"));
    }

    @Test
    void testSubtractFromWalletBalance_Success() throws Exception {
        // Given
        doNothing().when(walletService).subtractFromWalletBalance(1L, new BigDecimal("100.00"));

        // When & Then
        mockMvc.perform(post("/api/wallets/1/subtract")
                .param("amount", "100.00"))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount subtracted successfully"));
    }

    @Test
    void testGetWalletStats_Success() throws Exception {
        // Given
        BigDecimal averageBalance = new BigDecimal("500.00");
        BigDecimal totalSystemBalance = new BigDecimal("10000.00");
        when(walletService.getAverageWalletBalance()).thenReturn(averageBalance);
        when(walletService.getTotalSystemBalance()).thenReturn(totalSystemBalance);

        // When & Then
        mockMvc.perform(get("/api/wallets/stats"))
                .andExpect(status().isOk())
                .andExpect(content().string("Average Wallet Balance: 500.00, Total System Balance: 10000.00"));
    }

    @Test
    void testGetWalletsByCurrency_Success() throws Exception {
        // Given
        List<Wallet> wallets = Arrays.asList(testWallet);
        when(walletService.getWalletsByCurrency(1L)).thenReturn(wallets);
        when(walletService.convertToDTO(any(Wallet.class))).thenReturn(testWalletDTO);

        // When & Then
        mockMvc.perform(get("/api/wallets/currency/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Test Wallet"));
    }
}
