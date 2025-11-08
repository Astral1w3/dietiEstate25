package com.dietiestates2025.dieti.config;
import com.dietiestates2025.dieti.Service.JwtUtil;
import com.dietiestates2025.dieti.Service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import lombok.NonNull;
import java.io.IOException;


/**
 * Un filtro personalizzato di Spring Security che viene eseguito una volta per ogni richiesta HTTP.
 * Il suo scopo è intercettare le richieste in arrivo, verificare la presenza di un JSON Web Token (JWT)
 * nell'header 'Authorization', validarlo e, se valido, impostare l'autenticazione dell'utente
 * nel contesto di sicurezza di Spring (SecurityContext). Questo permette all'utente di essere
 * considerato "autenticato" per la durata della richiesta.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtUtil jwtUtil;

    private UserService userService;

    /**
     * Esegue la logica del filtro per ogni richiesta.
     *
     * @param request  L'oggetto HttpServletRequest che rappresenta la richiesta in arrivo.
     * @param response L'oggetto HttpServletResponse per la risposta.
     * @param filterChain  La catena di filtri, usata per passare la richiesta al filtro successivo.
     * @throws ServletException se si verifica un errore durante l'elaborazione.
     * @throws IOException se si verifica un errore di I/O.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. Estrae l'header "Authorization" dalla richiesta.
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        // 2. Controlla se l'header è presente e se inizia con "Bearer ".
        // Se non è così, il filtro non fa nulla e passa la richiesta al filtro successivo.
        // Questo permette alle richieste per endpoint pubblici (es. /login) di procedere senza token.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        // 3. Estrae il token JWT rimuovendo il prefisso "Bearer " (7 caratteri).
        jwt = authHeader.substring(7);

        // 4. Estrae l'email dell'utente (il "subject" del token) usando il JwtUtil.
        userEmail = jwtUtil.extractUsername(jwt);
        // 5. Controlla se l'email è stata estratta e se non c'è già un'autenticazione nel SecurityContext.
        // La seconda condizione (`getAuthentication() == null`) è importante per evitare di ripetere
        // la logica di autenticazione se l'utente è già stato autenticato in precedenza nella catena di filtri.
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userService.loadUserByUsername(userEmail);
            // 7. Valida il token: controlla che la firma sia valida, che non sia scaduto e che corrisponda all'utente.
            if (jwtUtil.validateToken(jwt, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 11. Passa sempre la richiesta al filtro successivo nella catena.
        filterChain.doFilter(request, response);
    }
}