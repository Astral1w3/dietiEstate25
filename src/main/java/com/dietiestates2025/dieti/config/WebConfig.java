package com.dietiestates2025.dieti.config; // Assicurati che il package sia corretto

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Applica a tutti gli endpoint
                .allowedOrigins("http://localhost:3000") // Permetti richieste da questo origine
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Permetti questi metodi HTTP
                .allowedHeaders("*") // Permetti tutte le intestazioni
                .allowCredentials(true);
    }
}