package com.dietiestates2025.dieti.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Applica la configurazione CORS definita nel bean 'corsConfigurationSource'
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable()) //NOSONAR: App is stateless, authentication is done via JWT in header
            .authorizeHttpRequests(auth -> auth
                // === ENDPOINT PUBBLICI (accessibili a tutti) ===
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/{propertyId}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/properties/*/increment-view").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/visits/property/*/booked-dates").permitAll()

                // === ENDPOINT PER UTENTI AUTENTICATI (qualsiasi ruolo) ===
                .requestMatchers(HttpMethod.POST, "/api/visits/book").authenticated()
                .requestMatchers("/api/offers/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()

                // === ENDPOINT PROTETTI PER RUOLI SPECIFICI (AGENT, MANAGER, ADMIN) ===
                // L'agente può vedere le prenotazioni relative alle sue proprietà
                .requestMatchers("/api/visits/agent-bookings").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers("/api/offers/agent-offers").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                // La dashboard è accessibile solo a questi ruoli
                .requestMatchers("/api/dashboard/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")

                // Creazione/modifica/cancellazione di proprietà
                .requestMatchers(HttpMethod.POST, "/api/properties").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                
                // La regola per DELETE era già corretta.
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                // AGGIUNTA: Protegge l'endpoint per contrassegnare una proprietà come venduta.
                .requestMatchers(HttpMethod.POST, "/api/properties/*/sold").hasAnyRole("AGENT", "MANAGER", "ADMIN")

                // === REGOLA FINALE ===
                // Qualsiasi altra richiesta che non è stata ancora definita richiede l'autenticazione.
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // --- INIZIO BLOCCO MODIFICATO ---
        
        // Sostituisci "<nome-casuale>.azurestaticapps.net" con l'URL REALE del tuo frontend!
        // Ho lasciato localhost:3000 per permetterti di continuare a sviluppare in locale.
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "https://gentle-cliff-05689dc03.3.azurestaticapps.net/" 
        ));
        
        // --- FINE BLOCCO MODIFICATO ---
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(List.of("*")); 
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }
}