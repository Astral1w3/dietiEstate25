package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.dto.OfferDetailsDTO;
import com.dietiestates2025.dieti.dto.OfferRequestDTO;
import com.dietiestates2025.dieti.dto.OfferResponseDTO;
import com.dietiestates2025.dieti.Service.OfferService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        
        String userEmail = authentication.getName();

        OfferResponseDTO createdOffer = offerService.createOffer(offerRequest, userEmail);
        
        return new ResponseEntity<>(createdOffer, HttpStatus.CREATED);
    }

    @GetMapping("/agent-offers")
    public ResponseEntity<List<OfferDetailsDTO>> getAgentOffers(Authentication authentication) {
        String agentEmail = authentication.getName();
        List<OfferDetailsDTO> offers = offerService.getOffersForAgent(agentEmail);
        return ResponseEntity.ok(offers);
    }

    @PostMapping("/{offerId}/accept")
    public ResponseEntity<OfferDetailsDTO> acceptOffer(@PathVariable Integer offerId) {
        OfferDetailsDTO updatedOffer = offerService.acceptOffer(offerId);
        return ResponseEntity.ok(updatedOffer);
    }

    @PostMapping("/{offerId}/decline")
    public ResponseEntity<OfferDetailsDTO> declineOffer(@PathVariable Integer offerId) {
        OfferDetailsDTO updatedOffer = offerService.declineOffer(offerId);
        return ResponseEntity.ok(updatedOffer);
    }
}