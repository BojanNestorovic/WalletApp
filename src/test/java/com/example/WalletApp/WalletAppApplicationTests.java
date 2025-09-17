package com.example.WalletApp;

import com.example.WalletApp.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = DemoAplication.class)
@Import(TestSecurityConfig.class)
@ActiveProfiles("test")
class WalletAppApplicationTests {

    @Test
    void contextLoads() {
    }
}
