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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {

    private final BookedVisitRepository bookedVisitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final DozerBeanMapper dozerBeanMapper;

    public VisitService(
        BookedVisitRepository bookedVisitRepository, 
        PropertyRepository propertyRepository, 
        UserRepository userRepository,
        DozerBeanMapper dozerBeanMapper
    ) {
        this.bookedVisitRepository = bookedVisitRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.dozerBeanMapper = dozerBeanMapper;
    }

    /**
     * Recupera tutte le date già prenotate per un immobile specifico.
     * @param propertyId L'ID dell'immobile.
     * @return Una lista di oggetti Date.
     */
    @Transactional(readOnly = true)
    public List<Date> getBookedDatesForProperty(Integer propertyId) {
        // Verifica se l'immobile esiste
        if (!propertyRepository.existsById(propertyId)) {
            throw new ResourceNotFoundException("Property not found with id: " + propertyId);
        }
        
        List<BookedVisit> visits = bookedVisitRepository.findByPropertyIdProperty(propertyId);
        
        // Estrai solo le date dalla lista di visite
        return visits.stream()
                     .map(BookedVisit::getVisitDate)
                     .collect(Collectors.toList());
    }

    /**
     * Crea una nuova prenotazione per una visita.
     * @param request DTO con i dati della richiesta.
     * @param userEmail L'email dell'utente autenticato che sta prenotando.
     * @return Il DTO della visita creata.
     */
    @Transactional
    public BookedVisitDTO createBookedVisit(BookVisitRequestDTO request, String userEmail) {
        // Trova l'immobile o lancia un'eccezione
        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + request.getPropertyId()));

        // Trova l'utente o lancia un'eccezione
        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        // (Opzionale ma raccomandato) Controlla se la data è già stata prenotata per evitare race condition
        // ...

        // Crea la nuova entità BookedVisit
        BookedVisit newVisit = BookedVisit.builder()
            .property(property)
            .user(user)
            .visitDate(request.getVisitDate())
            .build();

        // Salva nel database
        BookedVisit savedVisit = bookedVisitRepository.save(newVisit);

        // Mappa l'entità salvata in un DTO e restituiscila
        return dozerBeanMapper.map(savedVisit, BookedVisitDTO.class);
    }
}