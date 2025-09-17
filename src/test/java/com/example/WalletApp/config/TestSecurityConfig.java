package com.example.WalletApp.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test konfiguracija za Spring Security
 * 
 * Ova klasa definiše sigurnosnu konfiguraciju koja se koristi tokom testiranja.
 * Onemogućava CSRF zaštitu i dozvoljava pristup svim endpoint-ima bez autentifikacije.
 * Ovo je potrebno da bi testovi mogli da se izvršavaju bez potrebe za prijavom.
 * 
 * @author vuksta
 * @version 1.0
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    /**
     * Konfiguriše sigurnosni filter chain za testiranje
     * 
     * Ova metoda kreira sigurnosnu konfiguraciju koja:
     * - Onemogućava CSRF zaštitu (potrebno za testiranje)
     * - Dozvoljava pristup svim zahtevima bez autentifikacije
     * - Onemogućava frame options (potrebno za H2 konzolu)
     * 
     * @param http - HttpSecurity objekat za konfiguraciju
     * @return konfigurisan SecurityFilterChain
     * @throws Exception ako dođe do greške u konfiguraciji
     */
    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Onemogućava CSRF za testiranje
            .authorizeRequests()
                .anyRequest().permitAll()  // Dozvoljava pristup svim endpoint-ima
            .and()
            .headers().frameOptions().disable();  // Potrebno za H2 konzolu
        
        return http.build();
    }
}
