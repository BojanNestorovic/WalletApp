package com.example.WalletApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Security configuration - disables default security to allow session-based authentication.
 * Spring Security is used only for BCrypt password encoding.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .anyRequest().permitAll() // Allow all requests - using HttpSession for auth
            .and()
            .headers().frameOptions().disable(); // For H2 console
        
        return http.build();
    }
}
