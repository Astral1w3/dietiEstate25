package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.dto.OfferRequestDTO;
import com.dietiestates2025.dieti.dto.OfferResponseDTO;
import com.dietiestates2025.dieti.Service.OfferService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @PostMapping
    public ResponseEntity<OfferResponseDTO> createOffer(
            @Valid @RequestBody OfferRequestDTO offerRequest,
            Authentication authentication) {
        
        // Ottieni l'email dell'utente autenticato dal contesto di sicurezza
        String userEmail = authentication.getName();

        OfferResponseDTO createdOffer = offerService.createOffer(offerRequest, userEmail);
        
        return new ResponseEntity<>(createdOffer, HttpStatus.CREATED);
    }
}