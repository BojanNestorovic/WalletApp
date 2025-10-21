package com.example.WalletApp.dto;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * DTO for wallet-to-wallet transfers with currency conversion.
 */
public class TransferDTO {
    
    @NotNull(message = "Izvorni novčanik je obavezan")
    private Long fromWalletId;
    
    @NotNull(message = "Odredišni novčanik je obavezan")
    private Long toWalletId;
    
    @NotNull(message = "Iznos je obavezan")
    private BigDecimal amount;
    
    private String description;
    
    // Constructors
    public TransferDTO() {}
    
    public TransferDTO(Long fromWalletId, Long toWalletId, BigDecimal amount, String description) {
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.amount = amount;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getFromWalletId() { return fromWalletId; }
    public void setFromWalletId(Long fromWalletId) { this.fromWalletId = fromWalletId; }
    
    public Long getToWalletId() { return toWalletId; }
    public void setToWalletId(Long toWalletId) { this.toWalletId = toWalletId; }
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
