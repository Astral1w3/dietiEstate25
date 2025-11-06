package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.BookVisitRequestDTO;
import com.dietiestates2025.dieti.dto.BookedVisitDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.BookedVisit;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.BookedVisitRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.dietiestates2025.dieti.dto.BookingDetailsDTO; // <-- Importa il nuovo DTO
import com.dietiestates2025.dieti.repositories.DashboardRepository; // <-- Importa DashboardRepository

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {
    
    private final DashboardRepository dashboardRepository;
    private final BookedVisitRepository bookedVisitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final DozerBeanMapper dozerBeanMapper;

    public VisitService(
        BookedVisitRepository bookedVisitRepository, 
        PropertyRepository propertyRepository, 
        UserRepository userRepository,
        DozerBeanMapper dozerBeanMapper,
        DashboardRepository dashboardRepository 
    ) {
        this.bookedVisitRepository = bookedVisitRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.dozerBeanMapper = dozerBeanMapper;
        this.dashboardRepository = dashboardRepository;
    }
 @Transactional(readOnly = true)
    public List<Date> getBookedDatesForProperty(Integer propertyId) {
        // OTTIMIZZAZIONE 1: Invece di caricare intere entità BookedVisit
        // per poi estrarre solo la data, chiediamo al database solo la colonna
        // che ci serve. Questo è molto più efficiente.
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Proprietà non trovata con id: " + propertyId);
        }
        return bookedVisitRepository.findVisitDatesByPropertyId(propertyId);
    }

    @Transactional
    public BookedVisitDTO createBookedVisit(BookVisitRequestDTO request, String userEmail) {
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Proprietà non trovata con id: " + request.getPropertyId()));

        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + userEmail));

        // ROBUSTEZZA: Aggiungiamo il controllo per prevenire doppie prenotazioni.
        // Se una visita per quella proprietà in quella data esiste già, lanciamo
        // un'eccezione che il GlobalExceptionHandler trasformerà in un 409 Conflict.
        if (bookedVisitRepository.existsByPropertyIdPropertyAndVisitDate(request.getPropertyId(), request.getVisitDate())) {
            throw new IllegalStateException("La data richiesta per questa proprietà è già stata prenotata.");
        }

        BookedVisit newVisit = BookedVisit.builder()
            .property(property)
            .user(user)
            .visitDate(request.getVisitDate())
            .build();

        BookedVisit savedVisit = bookedVisitRepository.save(newVisit);
        return dozerBeanMapper.map(savedVisit, BookedVisitDTO.class);
    }

    @Transactional(readOnly = true)
    public List<BookingDetailsDTO> getBookingsForAgent(String agentEmail) {
        // La logica di base per trovare gli ID delle proprietà è corretta.
        List<Integer> propertyIds = dashboardRepository.findPropertyIdsByEmail(agentEmail);

        if (propertyIds.isEmpty()) {
            return Collections.emptyList();
        }

        // OTTIMIZZAZIONE 2 (N+1 QUERY): Invece di una query generica, usiamo una
        // query personalizzata con JOIN FETCH. Questo carica tutte le visite e le
        // relative entità (User, Property, Address) in UNA SOLA query, prevenendo
        // centinaia di chiamate al DB nel ciclo di mappatura.
        List<BookedVisit> visits = bookedVisitRepository.findWithDetailsByPropertyIds(propertyIds);

        return visits.stream()
                     .map(this::mapToBookingDetailsDTO)
                     .collect(Collectors.toList());
    }
    
    /**
     * Metodo helper per mappare una entità BookedVisit a un BookingDetailsDTO.
     */
    private BookingDetailsDTO mapToBookingDetailsDTO(BookedVisit visit) {
        User client = visit.getUser();
        Property property = visit.getProperty();
        String fullAddress = property.getAddress().getStreet() + ", " + property.getAddress().getMunicipality().getMunicipalityName();

        return BookingDetailsDTO.builder()
                .id_booking(visit.getIdBooking())
                .visit_date(visit.getVisitDate())
                .id_property(property.getIdProperty())
                .propertyAddress(fullAddress)
                .email(client.getEmail())
                .clientName(client.getUsername())
                .build();
    }
}