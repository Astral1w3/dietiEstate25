package com.dietiestates2025.dieti.controller;

import java.io.IOException;
import java.util.List;

// Import necessari
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication; // <-- IMPORT FONDAMENTALE
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper; // <-- IMPORT PER PARSARE IL JSON
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.dto.PropertyDTO;

@RestController
@RequestMapping("/api")
public class PropertyController {
    
    // Rendi i campi final per buona pratica
    private final PropertyService propertyService;
    private final ObjectMapper objectMapper; // <-- Inietta ObjectMapper, non Dozer

    // --- COSTRUTTORE CORRETTO E PULITO ---
    // Inietta solo ciò che serve a questo controller.
    // RegionService e MunicipalityService non erano usati.
    public PropertyController(PropertyService propertyService, ObjectMapper objectMapper){
        this.propertyService = propertyService;
        this.objectMapper = objectMapper; // <-- Assegna l'objectMapper
    }

    // --- METODO 'addProperty' UNICO E CORRETTO PER MULTIPART ---
    @PostMapping("/properties")
    public ResponseEntity<?> addProperty(
        @RequestPart("propertyData") String propertyDataJson, 
        @RequestPart("images") List<MultipartFile> images,
        Authentication authentication // <-- Spring Security inietterà questo oggetto automaticamente
    ) {
        // Controlla se l'utente è autenticato
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato.");
        }

        try {
            // 1. Usa objectMapper per convertire la stringa JSON in un DTO
            PropertyDTO propertyDTO = objectMapper.readValue(propertyDataJson, PropertyDTO.class);

            // 2. Recupera l'email (che è il 'name' nel Principal) dell'utente autenticato
            String userEmail = authentication.getName();

            // 3. Chiama il service, passando dati, file e l'identità dell'utente
            PropertyDTO savedPropertyDTO = propertyService.addPropertyAndImages(propertyDTO, images, userEmail); 
            
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);

        } catch (IOException e) {
            // Se il JSON non è valido, restituisci un errore chiaro
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel parsing dei dati della proprietà: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            // Se l'utente non viene trovato nel database (caso raro ma possibile)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // --- IL METODO DUPLICATO È STATO RIMOSSO ---
    // Non serve più, causava un errore di ambiguità all'avvio dell'applicazione.

    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<String> deleteProperty(@PathVariable int propertyId) {
        boolean deleted = propertyService.deleteProperty(propertyId);
        if (deleted) {
            return ResponseEntity.ok("Property eliminata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property non trovata.");
        }
    }

    @GetMapping("/properties/{propertyId}")
    public ResponseEntity<?> getPropertyById(@PathVariable int propertyId) {
        try {
            PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
            return ResponseEntity.ok(propertyDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    @GetMapping("/properties/search")
    public ResponseEntity<List<PropertyDTO>> searchPropertiesByLocation(@RequestParam String location) {
        List<PropertyDTO> properties = propertyService.findPropertiesByLocation(location);
        return ResponseEntity.ok(properties);
    }

  @PostMapping("/properties/{propertyId}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer propertyId) {
        // --- AGGIUNGI QUESTA RIGA PER IL DEBUG ---
        System.out.println("----------- DEBUG START -----------");
        System.out.println(">>> Richiesta RICEVUTA nel Controller per incrementViewCount.");
        System.out.println(">>> ID Proprietà: " + propertyId);
        System.out.println("----------- DEBUG END -----------");

        propertyService.incrementViewCount(propertyId);
        return ResponseEntity.ok().build();
    }
}