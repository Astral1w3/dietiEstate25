package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO;
import com.dietiestates2025.dieti.dto.RegisterRequestDTO; // <-- Importa il nuovo DTO
import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.model.Role;   // <-- Importa l'entità Role
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.RoleRepository; // <-- Importa il RoleRepository
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO; // <-- Importa il nuovo DTO
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // <-- Importa il PasswordEncoder
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository; // <-- Aggiungi RoleRepository
    private final PasswordEncoder passwordEncoder; // <-- Aggiungi PasswordEncoder
    private final DozerBeanMapper dozerBeanMapper;

    @Autowired // Usa @Autowired per l'iniezione tramite costruttore
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dozerBeanMapper = dozerBeanMapper;
    }
    
    public UserDTO registerUser(RegisterRequestDTO request) {
        // ... (i controlli iniziali su email e ruolo vanno bene) ...
        if (userRepository.findById(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email già in uso");
        }
        Role userRole = roleRepository.findByRoleName("User");
        if (userRole == null) {
            throw new RuntimeException("Ruolo di default 'USER' non trovato");
        }

        System.out.println(request);

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setRole(userRole);

        // --- INIZIA LA LOGICA MODIFICATA ---

        // Controlliamo se è una registrazione social (via Google) o standard
        if (request.getGoogleId() != null && !request.getGoogleId().isEmpty()) {
            // È una registrazione con Google
            newUser.setGoogleId(request.getGoogleId());
            // NON impostiamo una password locale, la lasciamo null
            // Il campo password nel DB deve permettere valori NULL
            newUser.setUserPassword(null);
        } else {
            // È una registrazione standard con password
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La password è obbligatoria per la registrazione standard.");
            }
            newUser.setUserPassword(passwordEncoder.encode(request.getPassword()));
        }

        // --- FINE DELLA LOGICA MODIFICATA ---

        System.out.println("Utente pronto per il salvataggio: ");
        System.out.println(newUser);
        User savedUser = userRepository.save(newUser);

        return dozerBeanMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + username));
        return new CustomUserDetails(user);
    }
    
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return dozerBeanMapper.map(user, UserDTO.class);
    }

    public boolean existsByEmail(String email) {
    return userRepository.findById(email).isPresent();
    }

    public void changeUserPassword(PasswordChangeRequestDTO request) {
        // 1. Trova l'utente nel database tramite l'email
        User user = userRepository.findById(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + request.getEmail()));

        // 2. Controlla se l'utente HA GIÀ una password locale
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            // Se l'utente ha già una password, è obbligatorio verificare quella corrente.
            if (request.getCurrentPassword() == null || !passwordEncoder.matches(request.getCurrentPassword(), user.getUserPassword())) {
                throw new BadCredentialsException("La password corrente non è corretta.");
            }
        }
        // Se user.getUserPassword() è null o vuoto (caso utente Google al primo set di password),
        // il controllo sulla vecchia password viene saltato, permettendo di impostarla direttamente.

        // 3. Controlla che la nuova password non sia vuota
        if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
            throw new IllegalArgumentException("La nuova password non può essere vuota.");
        }

        // 4. Codifica e imposta la nuova password
        user.setUserPassword(passwordEncoder.encode(request.getNewPassword()));

        // 5. Salva l'utente aggiornato nel database
        userRepository.save(user);

    }

// in UserService.java

    public boolean userHasPassword(String email) {
        // Trova l'utente come prima
        User user = userRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con email: " + email));
        
        String password = user.getUserPassword();

        // --- LOGICA CORRETTA E SICURA ---
        // Un utente ha una password se il campo non è nullo E non è una stringa vuota/spaziata.
        // Il controllo 'password != null' previene NullPointerException per gli utenti Google.
        return password != null && !password.isBlank();
    }
}