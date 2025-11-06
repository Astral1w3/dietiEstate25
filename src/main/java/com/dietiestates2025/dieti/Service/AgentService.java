package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.CreateUserWithRole;
import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Role;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.RoleRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// MODIFICA CHIAVE: La classe non estende più UserService.
@Service
public class AgentService {

    // Ora ha le sue dipendenze dirette, senza passare dal padre.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DozerBeanMapper dozerBeanMapper;

    // Il costruttore è ora molto più semplice.
    public AgentService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Metodo orchestratore per la creazione di un utente da parte di un agente/manager.
     */
    @Transactional
    public UserDTO createUserWithRole(CreateUserWithRole request) {
        // 1. Validazione preliminare
        validateEmailAvailability(request.getEmail());
        
        // 2. Recupero delle entità necessarie
        User creator = findCreator(request.getEmailCreator());
        Role roleToAssign = findAndValidateRole(request.getRoleName());

        // 3. Costruzione della nuova entità
        User newUser = buildNewUser(request, roleToAssign, creator);
        
        // 4. Persistenza
        User savedUser = userRepository.save(newUser);

        // 5. Mappatura a DTO per la risposta
        return dozerBeanMapper.map(savedUser, UserDTO.class);
    }

    // --- Metodi Helper Privati (Principio di Singola Responsabilità) ---

    private void validateEmailAvailability(String email) {
        if (userRepository.existsById(email)) {
            throw new IllegalStateException("Email già in uso: " + email); // Lancia 409 Conflict
        }
    }
    
    private User findCreator(String creatorEmail) {
        return userRepository.findById(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("L'utente creatore con email '" + creatorEmail + "' non è stato trovato."));
    }

    private Role findAndValidateRole(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Il ruolo specificato '" + roleName + "' non è valido."));
    }

    private User buildNewUser(CreateUserWithRole request, Role role, User creator) {
        if (request.getUserPassword() == null || request.getUserPassword().isBlank()) {
            throw new IllegalArgumentException("La password è obbligatoria."); // Lancia 400 Bad Request
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        newUser.setRole(role);
        
        // Logica di business chiave: assegna la stessa agenzia del creatore.
        newUser.setAgency(creator.getAgency());

        return newUser;
    }
}