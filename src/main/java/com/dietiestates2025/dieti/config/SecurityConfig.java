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
                // Endpoint pubblici (autenticazione, immagini, ricerca, etc.)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() 
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/search").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/{propertyId}").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/properties/*/increment-view").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/visits/property/*/booked-dates").permitAll()

                // Endpoint protetti che richiedono autenticazione
                .requestMatchers(HttpMethod.POST, "/api/visits/book").authenticated()

                // --- NUOVA REGOLA PER LE OFFERTE ---
                // Richiedi l'autenticazione per tutti gli endpoint sotto /api/offers/
                // Questo include POST per creare, GET per visualizzare, etc.
                .requestMatchers("/api/offers/**").authenticated()
                // --- FINE NUOVA REGOLA ---
                
                .requestMatchers("/api/user/**").authenticated()
                .requestMatchers("/api/dashboard/**").authenticated()
                
                // Qualsiasi altra richiesta richiede l'autenticazione
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
            
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000")); 
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")); 
        configuration.setAllowedHeaders(Arrays.asList("*")); 
        configuration.setAllowCredentials(true); 
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); 
        
        return source;
    }

}