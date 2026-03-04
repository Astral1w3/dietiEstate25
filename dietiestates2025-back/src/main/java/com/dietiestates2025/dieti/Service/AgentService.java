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

@Service
public class AgentService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DozerBeanMapper dozerBeanMapper;

    public AgentService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Metodo principale che orchestra la creazione di un nuovo utente con un ruolo specifico.
     * L'intera operazione è transazionale: se un qualsiasi passaggio fallisce, viene eseguito il rollback
     * di tutte le modifiche al database.
     *
     * @param request Il DTO {@link CreateUserWithRole} contenente i dati del nuovo utente,
     *                l'email del creatore e il nome del ruolo da assegnare.
     * @return Un {@link UserDTO} che rappresenta l'utente appena creato.
     * @throws IllegalStateException Se l'email fornita è già in uso (risulta in HTTP 409 Conflict).
     * @throws ResourceNotFoundException Se l'utente creatore o il ruolo specificato non esistono (HTTP 404 Not Found).
     * @throws IllegalArgumentException Se la password non è fornita (HTTP 400 Bad Request).
     */
    @Transactional
    public UserDTO createUserWithRole(CreateUserWithRole request) {
        validateEmailAvailability(request.getEmail());
        
        User creator = findCreator(request.getEmailCreator());
        Role roleToAssign = findAndValidateRole(request.getRoleName());

        User newUser = buildNewUser(request, roleToAssign, creator);
        
        User savedUser = userRepository.save(newUser);

        return dozerBeanMapper.map(savedUser, UserDTO.class);
    }

    // --- Metodi Helper Privati (Principio di Singola Responsabilità) ---

    /**
     * Verifica se un'email è già presente nel database.
     * @param email L'email da controllare.
     * @throws IllegalStateException se l'email esiste già.
     */
    private void validateEmailAvailability(String email) {
        if (userRepository.existsById(email)) {
            throw new IllegalStateException("Email già in uso: " + email);
        }
    }
    
    /**
     * Cerca e restituisce l'utente che sta eseguendo l'operazione di creazione.
     * @param creatorEmail L'email dell'utente creatore.
     * @return L'entità {@link User} del creatore.
     * @throws ResourceNotFoundException se non viene trovato alcun utente con l'email specificata.
     */
    private User findCreator(String creatorEmail) {
        return userRepository.findById(creatorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("L'utente creatore con email '" + creatorEmail + "' non è stato trovato."));
    }

    /**
     * Cerca e restituisce il ruolo da assegnare al nuovo utente.
     * @param roleName Il nome del ruolo (es. "AGENT", "MANAGER").
     * @return L'entità {@link Role} corrispondente.
     * @throws ResourceNotFoundException se non viene trovato alcun ruolo con il nome specificato.
     */
    private Role findAndValidateRole(String roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Il ruolo specificato '" + roleName + "' non è valido."));
    }

     /**
     * Costruisce e configura una nuova istanza dell'entità {@link User}.
     * Applica la logica di business fondamentale: il nuovo utente viene assegnato
     * alla stessa agenzia del suo creatore.
     *
     * @param request DTO con i dati del nuovo utente.
     * @param role L'entità Role da assegnare.
     * @param creator L'entità User del creatore, usata per recuperare l'agenzia.
     * @return Una nuova istanza di {@link User}, pronta per essere salvata.
     * @throws IllegalArgumentException se la password nel DTO è nulla o vuota.
     */
    private User buildNewUser(CreateUserWithRole request, Role role, User creator) {
        if (request.getUserPassword() == null || request.getUserPassword().isBlank()) {
            throw new IllegalArgumentException("La password è obbligatoria.");
        }

        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setUserPassword(passwordEncoder.encode(request.getUserPassword()));
        newUser.setRole(role);
        
        newUser.setAgency(creator.getAgency());

        return newUser;
    }
}