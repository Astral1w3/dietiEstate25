package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.VisitService;
import com.dietiestates2025.dieti.dto.BookVisitRequestDTO;
import com.dietiestates2025.dieti.dto.BookedVisitDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    /**
     * Endpoint per ottenere le date già prenotate per un immobile.
     * Questo endpoint può essere pubblico.
     */
    @GetMapping("/property/{propertyId}/booked-dates")
    public ResponseEntity<List<Date>> getBookedDates(@PathVariable Integer propertyId) {
        List<Date> bookedDates = visitService.getBookedDatesForProperty(propertyId);
        return ResponseEntity.ok(bookedDates);
    }

    /**
     * Endpoint per prenotare una nuova visita.
     * Questo endpoint deve essere protetto e richiede autenticazione.
     */
    @PostMapping("/book")
    public ResponseEntity<BookedVisitDTO> bookNewVisit(@RequestBody BookVisitRequestDTO request, Authentication authentication) {
        // Otteniamo l'email dell'utente autenticato dal principal di Spring Security
        String userEmail = authentication.getName();
        BookedVisitDTO createdVisit = visitService.createBookedVisit(request, userEmail);
        return ResponseEntity.ok(createdVisit);
    }
}