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
        
        return "=== PERSONAL FINANCE TRACKER - FULL STACK APLIKACIJA ===\n" +
               "Vreme: " + now.format(formatter) + "\n" +
               "Status: AKTIVAN\n" +
               "Verzija: 2.0.0 (Full Stack)\n\n" +
               
               "=== BAZA PODATAKA ===\n" +
               "Tip: H2 In-Memory\n" +
               "URL: jdbc:h2:mem:walletdb\n" +
               "Konzola: http://localhost:8080/api/h2-console\n\n" +
               
               "=== RESTful API ENDPOINTS ===\n\n" +
               
               "AUTH:\n" +
               "POST   /api/auth/register      - Registracija\n" +
               "POST   /api/auth/login         - Prijava\n" +
               "GET    /api/auth/current       - Trenutni korisnik\n" +
               "POST   /api/auth/logout        - Odjava\n\n" +
               
               "WALLETS:\n" +
               "GET    /api/wallets            - Lista novčanika\n" +
               "POST   /api/wallets            - Kreiraj novčanik\n" +
               "GET    /api/wallets/{id}       - Detalji novčanika\n" +
               "PUT    /api/wallets/{id}       - Ažuriraj novčanik\n" +
               "DELETE /api/wallets/{id}       - Obriši novčanik\n" +
               "PUT    /api/wallets/{id}/archive   - Arhiviraj\n\n" +
               
               "TRANSACTIONS:\n" +
               "GET    /api/transactions       - Lista transakcija\n" +
               "POST   /api/transactions       - Kreiraj transakciju\n" +
               "POST   /api/transactions/transfer - Transfer (konverzija)\n" +
               "DELETE /api/transactions/{id}  - Obriši transakciju\n\n" +
               
               "CATEGORIES:\n" +
               "GET    /api/categories         - Sve kategorije\n" +
               "POST   /api/categories         - Kreiraj kategoriju\n" +
               "DELETE /api/categories/{id}    - Obriši kategoriju\n\n" +
               
               "CURRENCIES:\n" +
               "GET    /api/currencies         - Sve valute\n" +
               "POST   /api/currencies         - Kreiraj valutu (admin)\n" +
               "PUT    /api/currencies/{id}    - Ažuriraj valutu (admin)\n" +
               "PUT    /api/currencies/{id}/update-from-api - API update\n\n" +
               
               "SAVINGS GOALS:\n" +
               "GET    /api/savings-goals      - Ciljevi štednje\n" +
               "POST   /api/savings-goals      - Kreiraj cilj\n" +
               "PUT    /api/savings-goals/{id} - Ažuriraj cilj\n" +
               "DELETE /api/savings-goals/{id} - Obriši cilj\n\n" +
               
               "ADMIN:\n" +
               "GET    /api/admin/users        - Svi korisnici\n" +
               "PUT    /api/admin/users/{id}/toggle-block - Blokiraj/odblokiraj\n" +
               "POST   /api/admin/users/{id}/notes - Dodaj belešku\n" +
               "GET    /api/admin/users/{id}/notes - Beleške o korisniku\n" +
               "GET    /api/admin/transactions - Sve transakcije\n" +
               "GET    /api/admin/dashboard    - Dashboard metrike\n\n" +
               
               "=== ARHITEKTURA ===\n" +
               "✓ Model-Service-Controller (3-tier)\n" +
               "✓ Dependency Injection (@Autowired)\n" +
               "✓ DTO Pattern\n" +
               "✓ @Transactional (atomične operacije)\n" +
               "✓ HttpSession (session-based auth)\n" +
               "✓ CORS sa credentials\n" +
               "✓ BCrypt password hashing\n\n" +
               
               "Frontend: Vue.js (http://localhost:5173)\n" +
               "Backend: Spring Boot (http://localhost:8080/api)";
    }

    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}
