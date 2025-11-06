package com.dietiestates2025.dieti.controller;

import com.dietiestates2025.dieti.Service.VisitService;
import com.dietiestates2025.dieti.dto.BookVisitRequestDTO;
import com.dietiestates2025.dieti.dto.BookedVisitDTO;
import com.dietiestates2025.dieti.dto.BookingDetailsDTO;
import org.springframework.http.HttpStatus; // Import aggiunto
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

    @GetMapping("/property/{propertyId}/booked-dates")
    public ResponseEntity<List<Date>> getBookedDates(@PathVariable Integer propertyId) {
        List<Date> bookedDates = visitService.getBookedDatesForProperty(propertyId);
        return ResponseEntity.ok(bookedDates);
    }

    @PostMapping("/book")
    public ResponseEntity<BookedVisitDTO> bookNewVisit(@RequestBody BookVisitRequestDTO request, Authentication authentication) {
        String userEmail = authentication.getName();
        BookedVisitDTO createdVisit = visitService.createBookedVisit(request, userEmail);
        
        // MODIFICA: Rispondere con 201 Created è lo standard REST
        // per indicare che una nuova risorsa è stata creata con successo.
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    @GetMapping("/agent-bookings")
    public ResponseEntity<List<BookingDetailsDTO>> getAgentBookings(Authentication authentication) {
        String agentEmail = authentication.getName();
        List<BookingDetailsDTO> bookings = visitService.getBookingsForAgent(agentEmail);
        return ResponseEntity.ok(bookings);
    }
}