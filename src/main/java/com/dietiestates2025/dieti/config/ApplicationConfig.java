package com.dietiestates2025.dieti.config;

import com.dietiestates2025.dieti.Service.UserService; // <-- 1. IMPORTA IL TUO USER SERVICE
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider; // <-- 2. IMPORTA AUTHENTICATION PROVIDER
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // <-- 3. IMPORTA L'IMPLEMENTAZIONE DAO
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class ApplicationConfig {

    // Questo bean è corretto e rimane invariato
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // Questo bean è corretto e rimane invariato
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Dice a Spring Security COME deve verificare le credenziali (username/password).
     * @param userService Il tuo servizio che implementa UserDetailsService per trovare gli utenti.
     * @param passwordEncoder L'encoder per verificare le password.
     * @return L'provider di autenticazione configurato.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
        // Usiamo l'implementazione standard DaoAuthenticationProvider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        // Collega il tuo servizio per caricare gli utenti
        authProvider.setUserDetailsService(userService);
        
        // Collega il tuo encoder per controllare le password
        authProvider.setPasswordEncoder(passwordEncoder);
        
        return authProvider;
    }
}