package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.AgentService;
import com.dietiestates2025.dieti.dto.CreateUserWithRole;
import com.dietiestates2025.dieti.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/management/users")
public class UserManagementController {
    
    private final AgentService agentService;

    public UserManagementController(AgentService agentService) {
        this.agentService = agentService;
    }

    /**
     * Endpoint per creare un nuovo utente con un ruolo specifico (es. AGENT, MANAGER).
     * L'utente creato viene automaticamente associato alla stessa agenzia del creatore.
     * Accessibile solo da ruoli con privilegi di gestione.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT', 'MANAGER', 'ADMIN')")
    public ResponseEntity<UserDTO> createUserWithRole(@RequestBody CreateUserWithRole request) {
        // Rimosso il blocco try-catch. Tutte le eccezioni (utente esistente,
        // ruolo non valido, etc.) saranno gestite dal GlobalExceptionHandler
        // per garantire risposte API coerenti (409 Conflict, 400 Bad Request, etc.).
        UserDTO createdUser = agentService.createUserWithRole(request);
        
        // Risposta 201 Created, come da standard REST per la creazione di risorse.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}