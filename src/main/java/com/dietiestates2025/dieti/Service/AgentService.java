package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.CreateUserWithRole;
import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.model.Role;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.RoleRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Corretto import per @Transactional

@Service // <-- ANNOTAZIONE MANCANTE: Indica a Spring di gestire questa classe come un servizio
public class AgentService extends UserService {

    // Queste dipendenze sono necessarie per la logica specifica di questo servizio.
    // Anche se il costruttore padre le inizializza, è buona norma averle qui
    // per un accesso diretto e per chiarezza.
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final DozerBeanMapper dozerBeanMapper;

    public AgentService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, DozerBeanMapper dozerBeanMapper) {
        // Chiama il costruttore della classe padre (UserService) per inizializzare le sue dipendenze
        super(userRepository, roleRepository, passwordEncoder, dozerBeanMapper);
        // Inizializza anche le variabili di questa classe specifica
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Crea un nuovo utente con un ruolo specifico e associa l'agenzia del creatore.
     * @param request Dati per la creazione dell'utente.
     * @return DTO dell'utente creato.
     */
    @Transactional // Assicura che l'intera operazione sia atomica
    public UserDTO createUserWithRole(CreateUserWithRole request) {
        // 1. Validazione dell'input
        if (userRepository.existsById(request.getEmail())) { // Più efficiente di findById().isPresent()
            throw new IllegalStateException("Email già in uso: ".concat(request.getEmail()));
        }
        if (request.getUserPassword() == null || request.getUserPassword().isBlank()) {
            throw new IllegalArgumentException("La password è obbligatoria.");
        }

        // --- CORREZIONE LOGICA ---
        // Trova l'utente creatore. Se non esiste, lancia un'eccezione.
        // Questo è il modo standard e più pulito in Java con Optional.
        User creatorUser = userRepository.findById(request.getEmailCreator())
                .orElseThrow(() -> new IllegalArgumentException("L'utente creatore non esiste."));

        // 2. Trova il ruolo specificato nel database
        Role roleToAssign = roleRepository.findByRoleName(request.getRoleName());
        if (roleToAssign == null) {
            throw new RuntimeException("Ruolo non valido: ".concat(request.getRoleName()));
        }

        // 3. Crea la nuova entità User
        User newUser = new User();
        newUser.setEmail(request.getEmail());
        newUser.setUsername(request.getUsername());
        newUser.setUserPassword(passwordEncoder.encode(request.getUserPassword())); // Codifica la password
        newUser.setRole(roleToAssign);
        
        // --- LOGICA CHIAVE IMPLEMENTATA ---
        // Associa al nuovo utente la stessa agenzia del suo creatore.
        newUser.setAgency(creatorUser.getAgency());

        // 4. Salva il nuovo utente nel database
        User savedUser = userRepository.save(newUser);

        // 5. Mappa l'entità salvata in un DTO e restituiscila
        return dozerBeanMapper.map(savedUser, UserDTO.class);
    }
}