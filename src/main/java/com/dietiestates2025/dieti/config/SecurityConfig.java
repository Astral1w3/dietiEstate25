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

/**
 * Classe di configurazione principale per Spring Security.
 * Abilita la sicurezza web per l'applicazione e definisce tutte le regole di accesso
 * per gli endpoint HTTP, la gestione delle sessioni, la configurazione CORS e l'integrazione
 * del filtro di autenticazione JWT personalizzato.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    /**
     * Definisce la catena di filtri di sicurezza che verrà applicata a tutte le richieste HTTP.
     * Questa è la configurazione centrale che determina "chi può fare cosa".
     *
     * @param http L'oggetto HttpSecurity usato per costruire la catena di filtri.
     * @return L'oggetto SecurityFilterChain costruito.
     * @throws Exception se si verifica un errore durante la configurazione.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) 
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/{propertyId}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/properties/*/increment-view").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/visits/property/*/booked-dates").permitAll()

                .requestMatchers(HttpMethod.POST, "/api/visits/book").authenticated()
                .requestMatchers("/api/offers/**").authenticated()
                .requestMatchers("/api/user/**").authenticated()

                .requestMatchers("/api/visits/agent-bookings").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers("/api/offers/agent-offers").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers("/api/dashboard/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/properties").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/properties/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                
                .requestMatchers(HttpMethod.DELETE, "/api/properties/**").hasAnyRole("AGENT", "MANAGER", "ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/properties/*/sold").hasAnyRole("AGENT", "MANAGER", "ADMIN")

                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    /**
     * Definisce la configurazione per il Cross-Origin Resource Sharing (CORS).
     * Questo bean è essenziale per permettere al frontend (che gira su un'origine diversa, es. localhost:3000)
     * di comunicare con il backend.
     *
     * @return La fonte di configurazione CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        
        configuration.setAllowedOrigins(List.of(
            "http://localhost:3000", 
            "https://gentle-cliff-05689dc03.3.azurestaticapps.net/" 
        ));
        
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(List.of("*")); 
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }
}