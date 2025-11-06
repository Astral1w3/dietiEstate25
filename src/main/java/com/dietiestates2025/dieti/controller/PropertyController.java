package com.dietiestates2025.dieti.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;

import com.dietiestates2025.dieti.Service.PropertyService;
import com.dietiestates2025.dieti.dto.ApiResponse;
import com.dietiestates2025.dieti.dto.PagedResponseDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.dto.PropertyStateUpdateDTO;

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
        PagedResponseDTO<PropertyDTO> response = propertyService.findPropertiesByLocationAvailable(location, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{propertyId}")
    public ResponseEntity<PropertyDTO> getPropertyById(@PathVariable int propertyId) {
        // Rimosso try-catch. Se la proprietà non viene trovata,
        // il service lancerà ResourceNotFoundException, che sarà
        // gestita dal nostro GlobalExceptionHandler.
        PropertyDTO propertyDTO = propertyService.getPropertyById(propertyId);
        return ResponseEntity.ok(propertyDTO);
    }

    @PostMapping
    public ResponseEntity<PropertyDTO> addProperty(
        @RequestPart("propertyData") String propertyDataJson, 
        @RequestPart("images") List<MultipartFile> images,
        Authentication authentication
    ) throws JsonProcessingException { // L'eccezione viene ora propagata
        
        // NOTA: Il controllo `if (authentication == null)` è stato rimosso.
        // Se questo endpoint è protetto da Spring Security, un utente non
        // autenticato non raggiungerà mai questo metodo. La sicurezza
        // viene gestita a un livello superiore, mantenendo il controller pulito.

        PropertyDTO propertyDTO = objectMapper.readValue(propertyDataJson, PropertyDTO.class);
        String userEmail = authentication.getName(); // Recupera l'email in modo sicuro
        
        PropertyDTO savedPropertyDTO = propertyService.addPropertyAndImages(propertyDTO, images, userEmail); 
        
        // Risposta 201 CREATED, standard per la creazione di risorse.
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPropertyDTO);
    }

    @DeleteMapping("/{propertyId}")
    public ResponseEntity<ApiResponse> deleteProperty(@PathVariable int propertyId) {
        // La logica è stata spostata nel service. Ora il service lancia
        // un'eccezione se la risorsa non esiste.
        // In caso di successo, non restituiamo più una stringa semplice ma la nostra
        // ApiResponse standard per coerenza.
        propertyService.deleteProperty(propertyId);
        return ResponseEntity.ok(new ApiResponse(true, "Proprietà eliminata con successo."));
    }
  
    @PostMapping("/{propertyId}/increment-view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Integer propertyId) {
        propertyService.incrementViewCount(propertyId);
        // ResponseEntity.accepted() (202) è semanticamente più corretto per
        // operazioni che non hanno un risultato immediato da restituire.
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/{propertyId}/state")
    public ResponseEntity<Void> updatePropertyState(
        @PathVariable int propertyId, 
        @Valid @RequestBody PropertyStateUpdateDTO stateUpdateDTO
    ) {
        propertyService.updatePropertyState(propertyId, stateUpdateDTO.getState());
        return ResponseEntity.ok().build(); // Restituisce 200 OK senza corpo
    }

}