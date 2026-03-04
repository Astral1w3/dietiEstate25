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

    @PostMapping
    @PreAuthorize("hasAnyRole('AGENT', 'MANAGER', 'ADMIN')")
    public ResponseEntity<UserDTO> createUserWithRole(@RequestBody CreateUserWithRole request) {
        UserDTO createdUser = agentService.createUserWithRole(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}