package com.example.WalletApp.dto;

import javax.validation.constraints.*;

public class CategoryDTO {
    private Long id;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 50, message = "Category name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Category type is required")
    private String type;
    
    private boolean predefined;
    private Long userId;
    
    // Constructors
    public CategoryDTO() {}
    
    public CategoryDTO(String name, String type, boolean predefined, Long userId) {
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
