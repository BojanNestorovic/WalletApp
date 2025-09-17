package com.example.WalletApp.dto;

import javax.validation.constraints.*;

public class CurrencyDTO {
    private Long id;
    
    @NotBlank(message = "Currency name is required")
    @Size(min = 3, max = 10, message = "Currency name must be between 3 and 10 characters")
    private String name;
    
    @NotNull(message = "Value to EUR is required")
    @DecimalMin(value = "0.01", message = "Value to EUR must be greater than 0")
    private Double valueToEur;
    
    // Constructors
    public CurrencyDTO() {}
    
    public CurrencyDTO(String name, Double valueToEur) {
        this.name = name;
        this.valueToEur = valueToEur;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Double getValueToEur() { return valueToEur; }
    public void setValueToEur(Double valueToEur) { this.valueToEur = valueToEur; }
}
