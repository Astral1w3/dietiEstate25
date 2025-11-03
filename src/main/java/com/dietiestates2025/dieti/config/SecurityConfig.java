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

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // --- AGGIUNTA: Inietta il tuo filtro JWT ---
    private final JwtAuthenticationFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/{propertyId}").permitAll()
                
                // --- MODIFICA CHIAVE ---
                // Dichiariamo esplicitamente che gli endpoint /user/** richiedono autenticazione.
                // Questo rende la configurazione più chiara.
                .requestMatchers("/api/user/**").authenticated()

                .anyRequest().authenticated()
            )
            // --- AGGIUNTA FONDAMENTALE ---
            // 1. Diciamo a Spring di non creare sessioni (tipico per API REST con JWT)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 2. Aggiungiamo il nostro filtro JWT prima del filtro di autenticazione standard
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    // 4. AGGIUNGI QUESTO BEAN PER DEFINIRE LA CONFIGURAZIONE CORS
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        // Aggiungo PATCH per completezza, dato che lo usi
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(Arrays.asList("*")); 
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }

}