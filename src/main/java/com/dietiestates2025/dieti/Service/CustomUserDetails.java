package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.model.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final User user; // Manteniamo l'oggetto utente originale per un facile accesso

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (user == null || user.getRole() == null || user.getRole() == null) {
            return Collections.emptyList();
        }

        // --- MODIFICA 1: Aggiungiamo il prefisso "ROLE_" come si aspetta Spring Security. ---
        // Usiamo anche toUpperCase() per coerenza, dato che i ruoli sono spesso in maiuscolo.
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
        // L'identificativo univoco per Spring è l'email
        return user.getEmail();
    }

    // --- MODIFICA 2: Aggiungiamo un getter per l'oggetto User originale ---
    // Questo ci servirà nel prossimo passo per accedere al nome del ruolo pulito.
    public User getUser() {
        return user;
    }


    // I seguenti metodi rimangono invariati
    @Override 
    public boolean isAccountNonExpired() { return true; }

    @Override 
    public boolean isAccountNonLocked() { return true; }

    @Override 
    public boolean isCredentialsNonExpired() { return true; }

    @Override 
    public boolean isEnabled() { return true; }
}