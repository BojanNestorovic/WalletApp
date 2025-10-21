package com.example.WalletApp.config;

import com.example.WalletApp.entity.Currency;
import com.example.WalletApp.entity.Role;
import com.example.WalletApp.entity.User;
import com.example.WalletApp.repository.CurrencyRepository;
import com.example.WalletApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Initializes default admin user on application startup.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user already exists
        if (!userRepository.existsByUsername("admin")) {
            // Get default currency (RSD - id: 1)
            Currency defaultCurrency = currencyRepository.findById(1L)
                    .orElse(null);

            // Create admin user
            User admin = new User();
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.setUsername("admin");
            admin.setEmail("admin@test.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setBirthDate(new Date());
            admin.setRole(Role.ADMINISTRATOR);
            admin.setCurrency(defaultCurrency);
            admin.setBlocked(false);

            userRepository.save(admin);
            System.out.println("✅ Admin user created: username=admin, password=admin123");
        } else {
            System.out.println("ℹ️  Admin user already exists");
        }
    }
}
