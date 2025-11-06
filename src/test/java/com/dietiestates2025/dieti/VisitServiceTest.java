package com.dietiestates2025.dieti;
import com.dietiestates2025.dieti.Service.VisitService;
import com.dietiestates2025.dieti.dto.BookVisitRequestDTO;
import com.dietiestates2025.dieti.dto.BookedVisitDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.*;
import com.dietiestates2025.dieti.repositories.*;
import org.dozer.DozerBeanMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock private BookedVisitRepository bookedVisitRepository;
    @Mock private PropertyRepository propertyRepository;
    @Mock private UserRepository userRepository;
    @Mock private DozerBeanMapper dozerBeanMapper;

    @InjectMocks private VisitService visitService;

    @Test
    void createBookedVisit_WhenDataIsValid_ShouldCreateVisit() {
        // GIVEN: Dati di richiesta validi, utente e proprietà esistenti, data non prenotata
        BookVisitRequestDTO request = new BookVisitRequestDTO(1, new Date());
        String userEmail = "client@example.com";
        
        Property property = new Property();
        property.setIdProperty(request.getPropertyId());
        
        User user = new User();
        user.setEmail(userEmail);
        
        when(propertyRepository.findById(request.getPropertyId())).thenReturn(Optional.of(property));
        when(userRepository.findById(userEmail)).thenReturn(Optional.of(user));
        when(bookedVisitRepository.existsByPropertyIdPropertyAndVisitDate(request.getPropertyId(), request.getVisitDate())).thenReturn(false);

        BookedVisit savedVisit = new BookedVisit(); // Simula l'oggetto salvato
        when(bookedVisitRepository.save(any(BookedVisit.class))).thenReturn(savedVisit);
        
        BookedVisitDTO expectedDto = new BookedVisitDTO(); // Simula il DTO mappato
        when(dozerBeanMapper.map(savedVisit, BookedVisitDTO.class)).thenReturn(expectedDto);

        // WHEN: Si tenta di creare la visita
        BookedVisitDTO result = visitService.createBookedVisit(request, userEmail);

        // THEN: La visita viene creata e il DTO restituito
        assertNotNull(result);
        assertEquals(expectedDto, result);
        verify(bookedVisitRepository).save(any(BookedVisit.class));
    }

    @Test
    void createBookedVisit_WhenPropertyNotFound_ShouldThrowResourceNotFoundException() {
        // GIVEN: Un ID proprietà non esistente
        BookVisitRequestDTO request = new BookVisitRequestDTO(999, new Date());
        when(propertyRepository.findById(999)).thenReturn(Optional.empty());

        // WHEN/THEN: Ci si aspetta l'eccezione
        assertThrows(ResourceNotFoundException.class, 
            () -> visitService.createBookedVisit(request, "client@example.com"));
    }
    
    @Test
    void createBookedVisit_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        // GIVEN: Una email di un utente non esistente
        BookVisitRequestDTO request = new BookVisitRequestDTO(1, new Date());
        when(propertyRepository.findById(1)).thenReturn(Optional.of(new Property()));
        when(userRepository.findById("ghost@example.com")).thenReturn(Optional.empty());

        // WHEN/THEN: Ci si aspetta l'eccezione
        assertThrows(ResourceNotFoundException.class, 
            () -> visitService.createBookedVisit(request, "ghost@example.com"));
    }

    @Test
    void createBookedVisit_WhenDateIsAlreadyBooked_ShouldThrowIllegalStateException() {
        // GIVEN: Una data già prenotata per una data proprietà
        BookVisitRequestDTO request = new BookVisitRequestDTO(1, new Date());
        when(propertyRepository.findById(1)).thenReturn(Optional.of(new Property()));
        when(userRepository.findById("client@example.com")).thenReturn(Optional.of(new User()));
        when(bookedVisitRepository.existsByPropertyIdPropertyAndVisitDate(request.getPropertyId(), request.getVisitDate())).thenReturn(true);

        // WHEN/THEN: Ci si aspetta l'eccezione
        assertThrows(IllegalStateException.class, 
            () -> visitService.createBookedVisit(request, "client@example.com"));
    }
}