package com.dietiestates2025.dieti.config;

import com.dietiestates2025.dieti.Service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Classe di configurazione centrale per l'applicazione, dedicata alla definizione dei bean
 * fondamentali per il funzionamento di Spring Security.
 * Rende disponibili componenti chiave come l'AuthenticationManager, il PasswordEncoder
 * e l'AuthenticationProvider, che sono il cuore del meccanismo di autenticazione.
 */
@Configuration
public class ApplicationConfig {

    /**
     * Espone l'{@link AuthenticationManager} come un bean gestito da Spring.
     * L'AuthenticationManager è il componente principale che orchestra il processo di autenticazione.
     * Viene ottenuto dalla configurazione standard di Spring Security, garantendo che sia
     * correttamente inizializzato con tutti i provider disponibili.
     *
     * @param authenticationConfiguration L'oggetto di configurazione fornito da Spring per accedere al manager.
     * @return L'AuthenticationManager configurato.
     * @throws Exception se si verifica un errore durante il recupero del manager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * Definisce il bean per la codifica delle password.
     * Utilizza {@link BCryptPasswordEncoder}, l'implementazione standard e raccomandata
     * per l'hashing delle password. BCrypt è un algoritmo robusto che include automaticamente
     * un "salt" per proteggere contro attacchi di tipo rainbow table.
     *
     * @return Un'istanza del PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Definisce il provider di autenticazione che si basa su un'origine dati (es. un database).
     * Questo bean collega la logica per trovare un utente (tramite {@link UserService})
     * con la logica per verificare la sua password (tramite {@link PasswordEncoder}).
     *
     * @param userService Il nostro servizio personalizzato che implementa l'interfaccia UserDetailsService
     *                    per caricare i dati dell'utente dal database.
     * @param passwordEncoder Il bean definito sopra per la verifica delle password.
     * @return Un'istanza di {@link DaoAuthenticationProvider} pronta per essere usata dall'AuthenticationManager.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(UserService userService, PasswordEncoder passwordEncoder) {
         // DaoAuthenticationProvider è l'implementazione standard di Spring Security per l'autenticazione basata su DAO.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        
        authProvider.setUserDetailsService(userService);
        
        authProvider.setPasswordEncoder(passwordEncoder);
        
        return authProvider;
    }
}