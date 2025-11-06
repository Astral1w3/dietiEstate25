package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.JwtUtil;
import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // Iniezione tramite costruttore: best practice
    public AuthController(AuthenticationManager authenticationManager, UserService userService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> createAuthenticationToken(@RequestBody AuthenticationRequest request) {
        // Il blocco try-catch è stato rimosso. Se le credenziali sono errate,
        // authenticationManager.authenticate lancerà BadCredentialsException,
        // che verrà gestita dal nostro GlobalExceptionHandler.
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        final UserDetails userDetails = userService.loadUserByUsername(request.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/google-login")
    public ResponseEntity<AuthenticationResponse> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        // La logica complessa di "login o registrazione" è stata spostata nel UserService.
        // Il controller ora ha solo una responsabilità: orchestrare.
        final UserDetails userDetails = userService.processGoogleLogin(googleLoginRequest);
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        // Anche qui, nessun try-catch. Se l'utente esiste già, userService.registerUser
        // lancerà IllegalStateException, gestita centralmente.
        UserDTO createdUser = userService.registerUser(registerRequest);
        
        // Manteniamo la risposta 201 CREATED che è corretta dal punto di vista semantico REST.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}