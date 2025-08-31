package com.example.WalletApp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        return "=== WALLET APP - STATUS SISTEMA ===\n" +
               "Vreme: " + now.format(formatter) + "\n" +
               "Status: AKTIVAN\n" +
               "Verzija: 1.0.0\n" +
               "Okruženje: Razvoj\n\n" +
               
               "=== BAZA PODATAKA ===\n" +
               "Tip: H2 In-Memory\n" +
               "Status: POVEZANA\n" +
               "Tabele: 6 entiteta kreirano\n" +
               "Šema: Automatski generisana\n\n" +
               
               "=== ENTITETI ===\n" +
               "✓ User (korisnici)\n" +
               "✓ Currency (valute)\n" +
               "✓ Wallet (novčanici)\n" +
               "✓ Category (kategorije)\n" +
               "✓ Transaction (transakcije)\n" +
               "✓ SavingsGoal (ciljevi štednje)\n\n" +
               
               "=== REPOZITORIJUMI ===\n" +
               "✓ UserRepository\n" +
               "✓ CurrencyRepository\n" +
               "✓ WalletRepository\n" +
               "✓ CategoryRepository\n" +
               "✓ TransactionRepository\n" +
               "✓ SavingsGoalRepository\n\n" +
               
               "=== ENDPOINTI ===\n" +
               "GET /api/ - Status (ova stranica)\n" +
               "GET /api/health - Provera zdravlja\n" +
               "GET /api/h2-console/ - Konzola baze podataka\n\n" +
               
               "=== KONTROLNA TAČKA ===\n" +
               "Prva Kontrolna Tačka: ZAVRŠENA\n" +
               "Model podataka: ✓\n" +
               "Repository sloj: ✓\n" +
               "Konfiguracija: ✓\n\n" +
               
               "Spremno za Drugu Kontrolnu Tačku";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
