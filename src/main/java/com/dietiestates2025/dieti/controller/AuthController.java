package com.dietiestates2025.dieti.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.dietiestates2025.dieti.dto.RegisterRequestDTO;
import com.dietiestates2025.dieti.dto.UserDTO;
import com.dietiestates2025.dieti.Service.JwtUtil;
import com.dietiestates2025.dieti.Service.UserService;
import com.dietiestates2025.dieti.dto.AuthenticationRequest;
import com.dietiestates2025.dieti.dto.AuthenticationResponse;
import com.dietiestates2025.dieti.dto.GoogleLoginRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil; // Utility per la generazione del token

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username o password errati");
        }

        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest googleLoginRequest) {
        try {
            // Controlla se l'utente esiste già nel database
            UserDTO user;
            if (userService.existsByEmail(googleLoginRequest.getEmail())) {
                // Se l'utente esiste, recuperalo
                user = userService.getUserByEmail(googleLoginRequest.getEmail());
            } else {
                // Se l'utente non esiste, registralo automaticamente
                RegisterRequestDTO registerRequest = new RegisterRequestDTO();
                registerRequest.setEmail(googleLoginRequest.getEmail());
                registerRequest.setUsername(googleLoginRequest.getName());
                registerRequest.setGoogleId(googleLoginRequest.getGoogleId()); // Usa l'ID Google come password temporanea
                user = userService.registerUser(registerRequest);
            }

            // Carica i dettagli dell'utente per generare il token JWT
            UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
            String jwt = jwtUtil.generateToken(userDetails);

            // Restituisci il token JWT al client
            return ResponseEntity.ok(new AuthenticationResponse(jwt));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante il login con Google");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            UserDTO createdUser = userService.registerUser(registerRequest);
            // Restituisce 201 Created con l'utente creato (senza password)
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalStateException e) {
            // Se l'utente esiste già (lanciato dal nostro servizio)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            // Per qualsiasi altro errore
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante la registrazione");
        }
    }
}