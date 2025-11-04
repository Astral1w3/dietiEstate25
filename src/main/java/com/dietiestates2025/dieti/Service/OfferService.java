package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.OfferRequestDTO;
import com.dietiestates2025.dieti.dto.OfferResponseDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Offer;
import com.dietiestates2025.dieti.model.OfferState;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.OfferRepository;
import com.dietiestates2025.dieti.repositories.OfferStatusRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OfferService {

    // Assumiamo che nel tuo database lo stato "Pending" abbia ID = 1
    private static final Integer PENDING_STATUS_ID = 1;

    private final OfferRepository offerRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final OfferStatusRepository offerStatusRepository;

    public OfferService(OfferRepository offerRepository, PropertyRepository propertyRepository, UserRepository userRepository, OfferStatusRepository offerStatusRepository) {
        this.offerRepository = offerRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.offerStatusRepository = offerStatusRepository;
    }

   @Transactional
    public OfferResponseDTO createOffer(OfferRequestDTO request, String userEmail) {
        // 1. Recupera le entità necessarie (codice invariato)
        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + userEmail));

        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Proprietà non trovata con id: " + request.getPropertyId()));

        OfferState pendingState = offerStatusRepository.findById(PENDING_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Pending' non trovato..."));
        
        // 2. Esegui la validazione di business (codice invariato)
        validateOffer(request, property);

        // 3. Crea e popola la nuova entità Offerta (PARTE MODIFICATA)
        Offer newOffer = Offer.builder()
                .property(property)
                .user(user)
                .offerPrice(request.getOfferPrice())
                .offerDate(new Date()) 
                .offerState(pendingState) // Imposta l'oggetto relazione
                .build();
        
        // 4. Salva l'offerta (codice invariato)
        Offer savedOffer = offerRepository.save(newOffer);

        // 5. Mappa e restituisci (codice invariato)
        return mapToResponseDTO(savedOffer);
    }

    private void validateOffer(OfferRequestDTO request, Property property) {
        // Un utente non può fare un'offerta su una proprietà in affitto
        boolean isForRent = property.getSaleTypes().stream()
                .anyMatch(st -> "rent".equalsIgnoreCase(st.getSaleType()));
        if (isForRent) {
            throw new IllegalArgumentException("Non è possibile fare offerte su una proprietà in affitto.");
        }

        // L'offerta deve essere inferiore al prezzo di listino
        if (request.getOfferPrice().compareTo(property.getPrice()) >= 0) {
            throw new IllegalArgumentException("L'offerta deve essere inferiore al prezzo attuale della proprietà.");
        }
    }

    private OfferResponseDTO mapToResponseDTO(Offer offer) {
        return OfferResponseDTO.builder()
                .idOffer(offer.getIdOffer())
                .offerDate(offer.getOfferDate())
                .offerPrice(offer.getOfferPrice())
                .status(offer.getOfferState().getState())
                .propertyId(offer.getProperty().getIdProperty())
                .userEmail(offer.getUser().getEmail())
                .build();
    }
}