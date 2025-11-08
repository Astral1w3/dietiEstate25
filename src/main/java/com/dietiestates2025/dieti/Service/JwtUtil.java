package com.dietiestates2025.dieti.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value; // <-- IMPORT NECESSARIO

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

/**
 * Service di utility per la gestione centralizzata dei JSON Web Tokens (JWT).
 * Questa classe è responsabile di tutte le operazioni relative ai JWT:
 * - Generazione di token per utenti autenticati.
 * - Estrazione di informazioni (claims) dai token.
 * - Validazione della firma e della scadenza dei token.
 */
@Service
public class JwtUtil {

    /**
     * La chiave segreta utilizzata per firmare e verificare i token JWT.
     */
    private final SecretKey signingKey;

    /**
     * Costruttore che inietta la chiave segreta JWT dalle proprietà dell'applicazione.
     * La chiave, memorizzata come stringa Base64URL, viene decodificata e trasformata
     * in un oggetto SecretKey sicuro, adatto per l'algoritmo HMAC-SHA.
     *
     * @param secretKey La chiave segreta in formato Base64URL, iniettata dalla property `APPLICATION_SECURITY_JWT_SECRET_KEY`.
     */
    public JwtUtil(@Value("${APPLICATION_SECURITY_JWT_SECRET_KEY}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    
    private SecretKey getSigningKey() {
        return this.signingKey;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * Metodo generico per estrarre una singola informazione (claim) da un token.
     * Utilizza una funzione per specificare quale claim estrarre.
     *
     * @param token Il JWT da analizzare.
     * @param claimsResolver una funzione che prende in input l'oggetto {@link Claims} e restituisce il valore desiderato.
     * @param <T> Il tipo di dato del claim da estrarre.
     * @return Il valore del claim estratto.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Estrae tutti i claims (il payload) da un JWT dopo averne verificato la firma.
     * @param token Il JWT da cui estrarre il payload.
     * @return L'oggetto {@link Claims} contenente tutte le informazioni del token.
     */
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

    /**
     * Genera un nuovo JWT per un utente autenticato, arricchendolo con claims personalizzati.
     * Questa è la funzione chiave che crea il token da inviare al client dopo il login.
     *
     * @param userDetails L'oggetto {@link UserDetails} che rappresenta l'utente.
     * @return Una stringa che rappresenta il JWT compatto e firmato.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        
        // Verifichiamo che `userDetails` sia un'istanza della nostra implementazione `CustomUserDetails`
        // per poter accedere all'oggetto `User` originale e ai suoi dati specifici.
        if (userDetails instanceof CustomUserDetails) {
            CustomUserDetails customUserDetails = (CustomUserDetails) userDetails;
            
            // Inseriamo claims personalizzati che il frontend potrà facilmente utilizzare.
            claims.put("email", customUserDetails.getUser().getEmail());
            claims.put("username", customUserDetails.getUser().getUsername());
            
            // Inseriamo il nome del ruolo "pulito" (senza il prefisso "ROLE_" e in minuscolo),
            // che è il formato atteso e più comodo per la logica del frontend.
            claims.put("role", customUserDetails.getUser().getRole().toLowerCase());
        }
        
        // Il "subject" del token rimane l'email, che è l'ID univoco per l'autenticazione.
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Metodo helper che costruisce fisicamente il JWT.
     * @param claims I claims personalizzati da includere nel payload.
     * @param subject Il soggetto del token (l'identificatore principale dell'utente).
     * @return La stringa del JWT firmato.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 ore
                .signWith(getSigningKey(), Jwts.SIG.HS256) // Firma il token con la chiave e l'algoritmo specificato (nuovo metodo jjwt)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}