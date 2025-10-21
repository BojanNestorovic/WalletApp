package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CurrencyDTO {
    
    private Long id;
    
    @NotBlank(message = "Naziv valute je obavezan")
    private String name;
    
    @NotNull(message = "Vrednost u odnosu na EUR je obavezna")
    private Double valueToEur;
    
    // Constructors
    public CurrencyDTO() {}
    
    public CurrencyDTO(Long id, String name, Double valueToEur) {
        this.id = id;
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
