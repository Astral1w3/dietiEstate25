package com.dietiestates2025.dieti.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

@Service
public class JwtUtil {

    private String SECRET_KEY = "yourGeneratedBase64Key-oJb8V8Yzuw8bFpQ8g7F2aK3eP1cV6sD0lB7jN9hT5yE=";

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                   .verifyWith(getSigningKey()) // NUOVO METODO
                   .build()
                   .parseSignedClaims(token)
                   .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // --- QUESTA È LA MODIFICA FONDAMENTALE PER IL FRONTEND ---
        // Controlliamo se stiamo lavorando con la nostra implementazione custom.
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            // Accediamo all'oggetto User originale per prendere i dati nel formato desiderato.
            claims.put("email", customUserDetails.getUser().getEmail());
            claims.put("username", customUserDetails.getUser().getUsername());
            
            // Prendiamo il nome del ruolo SENZA il prefisso "ROLE_" e lo mettiamo nel token.
            // Questo è ciò che vedrà il frontend!
            claims.put("role", customUserDetails.getUser().getRole().toLowerCase());
        }
        
        // Il "subject" del token rimane l'email (l'ID univoco per l'autenticazione).
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 ore
                .signWith(getSigningKey(), Jwts.SIG.HS256) // NUOVO METODO
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}