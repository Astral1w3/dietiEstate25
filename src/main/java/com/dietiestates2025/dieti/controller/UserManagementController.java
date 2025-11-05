package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.AgentService; // Iniettiamo il servizio base
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
     * Endpoint per creare un nuovo utente.
     * Accessibile da AGENT, MANAGER, e ADMIN.
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT', 'MANAGER', 'ADMIN')")
    public ResponseEntity<?> createUserWithRole(@RequestBody CreateUserWithRole request) {
        try {
            UserDTO createdUser = agentService.createUserWithRole(request);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}