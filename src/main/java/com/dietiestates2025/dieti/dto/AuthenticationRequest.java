package com.dietiestates2025.dieti.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Genera getter, setter, toString(), equals() e hashCode()
@NoArgsConstructor // Genera un costruttore senza argomenti
@AllArgsConstructor // Genera un costruttore con tutti gli argomenti
public class AuthenticationRequest {
    private String username;
    private String password;
}