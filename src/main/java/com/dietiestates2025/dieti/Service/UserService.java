package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.PasswordChangeRequestDTO;
import com.dietiestates2025.dieti.dto.GoogleLoginRequest; 
import com.dietiestates2025.dieti.dto.RegisterRequestDTO;
import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.BuyingAndSelling;
import com.dietiestates2025.dieti.model.Role;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.RoleRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    // Costante per evitare "magic strings"
    private static final String DEFAULT_USER_ROLE = "User";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DozerBeanMapper dozerBeanMapper;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = findUserByEmail(email);
        return new CustomUserDetails(user); // Assumendo che CustomUserDetails esista
    }

    @Transactional
    public UserDTO registerUser(RegisterRequestDTO request) {
        if (userRepository.existsById(request.getEmail())) {
            throw new IllegalStateException("Email già in uso");
        }

        Role userRole = roleRepository.findByRoleName(DEFAULT_USER_ROLE)
            .orElseThrow(() -> new RuntimeException("Ruolo di default '" + DEFAULT_USER_ROLE + "' non trovato nel database."));

        if (userRole == null) {
            throw new RuntimeException("Ruolo di default '" + DEFAULT_USER_ROLE + "' non trovato");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setRole(userRole);

        // Distingue tra registrazione social e standard
        if (request.getGoogleId() != null && !request.getGoogleId().isEmpty()) {
            newUser.setGoogleId(request.getGoogleId());
            newUser.setUserPassword(null); // La password è null per gli utenti social
        } else {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                throw new IllegalArgumentException("La password è obbligatoria per la registrazione standard.");
            }
            newUser.setUserPassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(newUser);
        return dozerBeanMapper.map(savedUser, UserDTO.class);
    }

    public UserDTO getUserByEmail(String email) {
        User user = findUserByEmail(email);
        return dozerBeanMapper.map(user, UserDTO.class);
    }

    @Transactional
    public void changeUserPassword(PasswordChangeRequestDTO request) {
        User user = findUserByEmail(request.getEmail());

        // Verifica la password corrente, se l'utente ne ha già una
        validateCurrentPassword(request.getCurrentPassword(), user);

        // Imposta la nuova password
        setNewPassword(user, request.getNewPassword());

        userRepository.save(user);
    }

    private void validateCurrentPassword(String currentPassword, User user) {
        // Se l'utente ha già una password, quella fornita deve essere corretta.
        if (user.getUserPassword() != null && !user.getUserPassword().isEmpty()) {
            if (currentPassword == null || !passwordEncoder.matches(currentPassword, user.getUserPassword())) {
                throw new BadCredentialsException("La password corrente non è corretta.");
            }
        }
        // Se l'utente non ha una password (es. login social), si può procedere senza verifica.
    }

    private void setNewPassword(User user, String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("La nuova password non può essere vuota.");
        }
        user.setUserPassword(passwordEncoder.encode(newPassword));
    }

    public boolean userHasPassword(String email) {
        User user = findUserByEmail(email);
        String password = user.getUserPassword();
        return password != null && !password.isBlank();
    }

    // Metodo helper per trovare un utente o lanciare un'eccezione standard
    private User findUserByEmail(String email) {
        return userRepository.findById(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + email));
    }

    // --- Logica di business spostata dall'entità User ---

    public List<BuyingAndSelling> getSellingsForUser(String email) {
        User user = findUserByEmail(email);
        String roleName = user.getRole().toUpperCase();
        if ("AGENT".equals(roleName) || "ADMIN".equals(roleName) || "MANAGER".equals(roleName)) {
            return user.getSellings();
        }
        return Collections.emptyList();
    }

    public List<BuyingAndSelling> getBuyingsForUser(String email) {
        User user = findUserByEmail(email);
        if ("USER".equalsIgnoreCase(user.getRole())) {
            return user.getBuyings();
        }
        return Collections.emptyList();
    }


    /**
     * Gestisce il login tramite Google.
     * Cerca un utente tramite email. Se esiste, lo restituisce.
     * Altrimenti, ne crea uno nuovo con i dati di Google e lo restituisce.
     * @param googleLoginRequest DTO con i dati provenienti dal login di Google.
     * @return UserDetails per l'utente trovato o appena creato.
     */
    @Transactional
    public UserDetails processGoogleLogin(GoogleLoginRequest googleLoginRequest) {
        // Cerca l'utente nel database. Optional<User> è più sicuro di exists + find.
        User user = userRepository.findById(googleLoginRequest.getEmail())
            .orElseGet(() -> {
                // Se l'utente non esiste (.orElseGet), crea e registra un nuovo utente.
                Role userRole = roleRepository.findByRoleName(DEFAULT_USER_ROLE)
                    .orElseThrow(() -> new RuntimeException("Ruolo di default '" + DEFAULT_USER_ROLE + "' non trovato nel database."));
                    
                 if (userRole == null) {
                    throw new RuntimeException("Ruolo di default '" + DEFAULT_USER_ROLE + "' non trovato");
                }

                User newUser = new User();
                newUser.setEmail(googleLoginRequest.getEmail());
                newUser.setUsername(googleLoginRequest.getName());
                newUser.setGoogleId(googleLoginRequest.getGoogleId());
                newUser.setRole(userRole);
                newUser.setUserPassword(null); // Nessuna password locale per gli utenti Google

                return userRepository.save(newUser);
            });

        // Restituisce i dettagli dell'utente (trovato o nuovo) per la creazione del JWT.
        return new CustomUserDetails(user); // Assumendo che CustomUserDetails esista
    }
}