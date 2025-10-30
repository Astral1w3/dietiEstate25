package com.dietiestates2025.dieti.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter // Genera solo il getter
@AllArgsConstructor // Genera un costruttore con tutti gli argomenti
public class AuthenticationResponse {
    private final String jwt;
}