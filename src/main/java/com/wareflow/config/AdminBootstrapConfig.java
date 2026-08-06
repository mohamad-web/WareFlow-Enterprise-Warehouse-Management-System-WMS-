package com.wareflow.config;

import com.wareflow.bootstrap.AdminBootstrapService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminBootstrapConfig {

    @Bean
    CommandLineRunner createInitialAdmin(
            AdminBootstrapService adminBootstrapService
    ) {
        return args -> adminBootstrapService.createAdminIfMissing();
    }
}