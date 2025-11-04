package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.OfferDetailsDTO;
import com.dietiestates2025.dieti.dto.OfferRequestDTO;
import com.dietiestates2025.dieti.dto.OfferResponseDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Dashboard;
import com.dietiestates2025.dieti.model.Offer;
import com.dietiestates2025.dieti.model.OfferState;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.OfferRepository;
import com.dietiestates2025.dieti.repositories.OfferStatusRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {
    private static final Integer PENDING_STATUS_ID = 1;
    private static final Integer ACCEPTED_STATUS_ID = 2;
    private static final Integer DECLINED_STATUS_ID = 3;

    private final OfferRepository offerRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final OfferStatusRepository offerStatusRepository;
    private final DashboardRepository dashboardRepository; 

    public OfferService(OfferRepository offerRepository, PropertyRepository propertyRepository, UserRepository userRepository, OfferStatusRepository offerStatusRepository, DashboardRepository dashboardRepository) {
        this.offerRepository = offerRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
        this.offerStatusRepository = offerStatusRepository;
        this.dashboardRepository = dashboardRepository;
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

      // --- INIZIO NUOVO METODO PER LA DASHBOARD ---
    @Transactional(readOnly = true)
    public List<OfferDetailsDTO> getOffersForAgent(String agentEmail) {
        // 1. Trova il dashboard dell'agente per accedere alle sue proprietà
        Dashboard agentDashboard = dashboardRepository.findById(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovato per l'agente: " + agentEmail));
        
        if (agentDashboard.getProperties() == null || agentDashboard.getProperties().isEmpty()) {
            return Collections.emptyList();
        }

        // 2. Estrai gli ID di tutte le proprietà dell'agente
        List<Integer> propertyIds = agentDashboard.getProperties().stream()
                .map(Property::getIdProperty)
                .collect(Collectors.toList());

        // 3. Trova tutte le offerte per quelle proprietà
        List<Offer> offers = offerRepository.findByPropertyIdPropertyIn(propertyIds);

        // 4. Mappa le entità Offer nel DTO dettagliato
        return offers.stream()
                     .map(this::mapToOfferDetailsDTO)
                     .collect(Collectors.toList());
    }

    private OfferDetailsDTO mapToOfferDetailsDTO(Offer offer) {
        User client = offer.getUser();
        Property property = offer.getProperty();
        String fullAddress = property.getAddress().getStreet() + ", " + property.getAddress().getMunicipality().getMunicipalityName();

        return OfferDetailsDTO.builder()
                .id_offer(offer.getIdOffer())
                .offer_price(offer.getOfferPrice())
                .offer_date(offer.getOfferDate())
                .state(offer.getOfferState().getState())
                .id_property(property.getIdProperty())
                .propertyAddress(fullAddress)
                .listingPrice(property.getPrice()) // Prendiamo il prezzo dalla proprietà
                .clientName(client.getUsername())
                .clientEmail(client.getEmail())
                .build();
    }

    @Transactional
    public OfferDetailsDTO acceptOffer(Integer offerId) {
        Offer offerToAccept = findOfferAndVerifyState(offerId);
        
        // Trova lo stato "Accepted"
        OfferState acceptedState = offerStatusRepository.findById(ACCEPTED_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Accepted' non trovato nel database."));
        
        // Aggiorna lo stato dell'offerta
        offerToAccept.setOfferState(acceptedState);
        Offer updatedOffer = offerRepository.save(offerToAccept);

        // LOGICA DI BUSINESS: Rifiuta automaticamente le altre offerte per la stessa proprietà
        declineOtherPendingOffers(updatedOffer);

        return mapToOfferDetailsDTO(updatedOffer);
    }
    @Transactional
    public OfferDetailsDTO declineOffer(Integer offerId) {
        Offer offerToDecline = findOfferAndVerifyState(offerId);

        // Trova lo stato "Declined"
        OfferState declinedState = offerStatusRepository.findById(DECLINED_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Declined' non trovato nel database."));
        
        // Aggiorna lo stato
        offerToDecline.setOfferState(declinedState);
        Offer updatedOffer = offerRepository.save(offerToDecline);
        
        return mapToOfferDetailsDTO(updatedOffer);
    }

    /** Metodo helper per trovare un'offerta e verificare che sia 'Pending' */
    private Offer findOfferAndVerifyState(Integer offerId) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new ResourceNotFoundException("Offerta non trovata con ID: " + offerId));

        if (!offer.getOfferState().getId().equals(PENDING_STATUS_ID)) {
            throw new IllegalStateException("È possibile modificare solo offerte con stato 'Pending'.");
        }
        return offer;
    }

   private void declineOtherPendingOffers(Offer acceptedOffer) {
    Integer propertyId = acceptedOffer.getProperty().getIdProperty();
    
    List<Offer> allOffersForProperty = offerRepository.findByPropertyIdProperty(propertyId);
    
    OfferState declinedState = offerStatusRepository.findById(DECLINED_STATUS_ID)
        .orElseThrow(() -> new IllegalStateException("Stato 'Declined' non trovato."));

    for (Offer otherOffer : allOffersForProperty) {
        if (!otherOffer.getIdOffer().equals(acceptedOffer.getIdOffer()) && 
            otherOffer.getOfferState().getId().equals(PENDING_STATUS_ID)) 
        {
            otherOffer.setOfferState(declinedState);
            offerRepository.save(otherOffer);
        }
    }
}
}