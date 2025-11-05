package com.dietiestates2025.dieti.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.dto.PagedResponseDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;

@RestController
@RequestMapping("/api/properties")
public class PropertyController {
    
    private final PropertyService propertyService;
    private final ObjectMapper objectMapper;

    public PropertyController(PropertyService propertyService, ObjectMapper objectMapper){
        this.propertyService = propertyService;
        this.objectMapper = objectMapper;
    }

    @GetMapping(path = "/search", params = "location") 
    public ResponseEntity<PagedResponseDTO<PropertyDTO>> findPropertiesByLocationAvailable(
        @RequestParam String location,
        Pageable pageable
    ) {
        // La chiamata al service ora restituisce il nostro DTO personalizzato
        PagedResponseDTO<PropertyDTO> response = propertyService.findPropertiesByLocationAvailable(location, pageable);
        return ResponseEntity.ok(response);
    }

    
    // --- FINE MODIFICA ---

    // Metodo per ottenere una singola proprietà per ID.
    // Ora non entrerà più in conflitto con /search.
    @GetMapping("/{propertyId}")
    public ResponseEntity<?> getPropertyById(@PathVariable int propertyId) {
        try {
            PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
            return ResponseEntity.ok(propertyDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    
    // ... il resto del controller rimane identico ...

    @PostMapping
    public ResponseEntity<?> addProperty(
        @RequestPart("propertyData") String propertyDataJson, 
        @RequestPart("images") List<MultipartFile> images,
        Authentication authentication
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Utente non autenticato.");
        }
        try {
            PropertyDTO propertyDTO = objectMapper.readValue(propertyDataJson, PropertyDTO.class);
            String userEmail = authentication.getName();
            PropertyDTO savedPropertyDTO = propertyService.addPropertyAndImages(propertyDTO, images, userEmail); 
            return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Errore nel parsing dei dati della proprietà: " + e.getMessage());
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<String> deleteProperty(@PathVariable int propertyId) {
        boolean deleted = propertyService.deleteProperty(propertyId);
        if (deleted) {
            return ResponseEntity.ok("Property eliminata con successo.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Property non trovata.");
        }
    }
  
    @PostMapping("/{propertyId}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer propertyId) {
        propertyService.incrementViewCount(propertyId);
        return ResponseEntity.ok().build();
    }
}