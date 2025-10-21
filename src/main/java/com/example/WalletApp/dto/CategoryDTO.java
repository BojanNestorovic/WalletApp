package com.example.WalletApp.dto;

import javax.validation.constraints.NotBlank;

public class CategoryDTO {
    
    private Long id;
    
    @NotBlank(message = "Naziv kategorije je obavezan")
    private String name;
    
    @NotBlank(message = "Tip kategorije je obavezan")
    private String type; // INCOME or EXPENSE
    
    private boolean predefined;
    private Long userId;
    
    // Constructors
    public CategoryDTO() {}
    
    public CategoryDTO(Long id, String name, String type, boolean predefined, Long userId) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.predefined = predefined;
        this.userId = userId;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public boolean isPredefined() { return predefined; }
    public void setPredefined(boolean predefined) { this.predefined = predefined; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
