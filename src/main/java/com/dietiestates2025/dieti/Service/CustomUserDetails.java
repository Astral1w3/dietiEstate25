package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Implementazione personalizzata dell'interfaccia {@link UserDetails} di Spring Security.
 * Questa classe agisce come un "adapter" (adattatore) tra il nostro modello di dominio {@link User}
 * e l'oggetto che Spring Security utilizza internamente per gestire l'autenticazione e l'autorizzazione.
 * Invece di lavorare direttamente con l'oggetto User, Spring Security lavora con questa rappresentazione.
 */
public class CustomUserDetails implements UserDetails {

    /**
     * L'entità User originale dal nostro database.
     * Viene mantenuta per accedere facilmente a tutte le informazioni dell'utente,
     * come password, email, ruolo e altri dettagli.
     */
    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Restituisce le autorità (ruoli/permessi) concesse all'utente.
     * Spring Security utilizza questa collezione per le verifiche di autorizzazione (es. @PreAuthorize("hasRole('ADMIN')")).
     *
     * @return Una collezione contenente le autorità dell'utente.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getRole() == null || user.getRole() == null) {
            return Collections.emptyList();
        }

        String roleName = "ROLE_" + user.getRole().toUpperCase();
        
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        return Collections.singletonList(authority);
    }

    @Override
    public String getPassword() {
        return user.getUserPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }


    @Override 
    public boolean isAccountNonExpired() { return true; }

    @Override 
    public boolean isAccountNonLocked() { return true; }

    @Override 
    public boolean isCredentialsNonExpired() { return true; }

    @Override 
    public boolean isEnabled() { return true; }
}