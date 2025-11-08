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

    /**
    * Crea una nuova offerta per una proprietà.
    * L'operazione è transazionale per garantire che tutte le operazioni (lettura, validazione, scrittura)
    * vengano completate con successo o annullate.
    *
    * @param request Il DTO {@link OfferRequestDTO} con i dati della nuova offerta.
    * @param userEmail L'email dell'utente che effettua l'offerta, ottenuta dal contesto di sicurezza.
    * @return Un {@link OfferResponseDTO} che rappresenta l'offerta appena creata.
    * @throws ResourceNotFoundException Se l'utente o la proprietà non vengono trovati.
    * @throws IllegalArgumentException Se l'offerta viola le regole di business (es. su una proprietà in affitto).
    */
   @Transactional
    public OfferResponseDTO createOffer(OfferRequestDTO request, String userEmail) {
        User user = userRepository.findById(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Utente non trovato con email: " + userEmail));

        Property property = propertyRepository.findById(request.getPropertyId())
            .orElseThrow(() -> new ResourceNotFoundException("Proprietà non trovata con id: " + request.getPropertyId()));

        OfferState pendingState = offerStatusRepository.findById(PENDING_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Pending' non trovato..."));
        
            // 2. Esegue la validazione secondo le regole di business definite.
        validateOffer(request, property);

        // 3. Crea e popola la nuova entità Offerta utilizzando il pattern Builder per maggiore chiarezza.
        Offer newOffer = Offer.builder()
                .property(property)
                .user(user)
                .offerPrice(request.getOfferPrice())
                .offerDate(new Date()) 
                .offerState(pendingState)
                .build();
        // 4. Salva la nuova offerta nel database.
        Offer savedOffer = offerRepository.save(newOffer);
        // 5. Mappa l'entità salvata in un DTO di risposta da inviare al client.
        return mapToResponseDTO(savedOffer);
    }

     /**
     * Metodo helper privato per incapsulare le regole di validazione di un'offerta.
     * @param request Il DTO dell'offerta.
     * @param property L'entità della proprietà a cui si riferisce l'offerta.
     * @throws IllegalArgumentException se una regola non viene rispettata.
     */
    private void validateOffer(OfferRequestDTO request, Property property) {
        // Regola 1: Non è possibile fare offerte su proprietà in affitto.
        boolean isForRent = property.getSaleTypes().stream()
                .anyMatch(st -> "rent".equalsIgnoreCase(st.getSaleType()));
        if (isForRent) {
            throw new IllegalArgumentException("Non è possibile fare offerte su una proprietà in affitto.");
        }
        // Regola 2: L'offerta deve essere inferiore al prezzo di listino.
        if (request.getOfferPrice().compareTo(property.getPrice()) >= 0) {
            throw new IllegalArgumentException("L'offerta deve essere inferiore al prezzo attuale della proprietà.");
        }
    }
    /**
     * Metodo helper per convertire un'entità Offer in un DTO di risposta semplice.
     * @param offer L'entità da mappare.
     * @return Il {@link OfferResponseDTO} corrispondente.
     */
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

    /**
     * Recupera tutte le offerte ricevute per le proprietà di un agente specifico.
     * Ottimizzato per recuperare i dati necessari per la visualizzazione nella dashboard dell'agente.
     *
     * @param agentEmail L'email dell'agente.
     * @return Una lista di {@link OfferDetailsDTO} con informazioni dettagliate su ogni offerta.
     */
    @Transactional(readOnly = true)
    public List<OfferDetailsDTO> getOffersForAgent(String agentEmail) {
        // 1. Trova il dashboard dell'agente per ottenere la lista delle sue proprietà.
        Dashboard agentDashboard = dashboardRepository.findById(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovato per l'agente: " + agentEmail));
        
        if (agentDashboard.getProperties() == null || agentDashboard.getProperties().isEmpty()) {
            return Collections.emptyList();
        }
        // 2. Estrae gli ID di tutte le proprietà dell'agente.
        List<Integer> propertyIds = agentDashboard.getProperties().stream()
                .map(Property::getIdProperty)
                .collect(Collectors.toList());
        // 3. Esegue una singola query per trovare tutte le offerte associate a quella lista di ID.
        List<Offer> offers = offerRepository.findByPropertyIdPropertyIn(propertyIds);
        // 4. Mappa ogni entità Offer in un DTO dettagliato per la risposta.
        return offers.stream()
                     .map(this::mapToOfferDetailsDTO)
                     .collect(Collectors.toList());
    }

    /**
     * Metodo helper per mappare un'entità Offer in un DTO dettagliato per la dashboard.
     * @param offer L'entità da mappare.
     * @return Il {@link OfferDetailsDTO} arricchito con dettagli sulla proprietà e sul cliente.
     */
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
                .listingPrice(property.getPrice())
                .clientName(client.getUsername())
                .clientEmail(client.getEmail())
                .build();
    }

    /**
     * Accetta un'offerta. Quando un'offerta viene accettata, tutte le altre offerte
     * in stato "Pending" per la stessa proprietà vengono automaticamente rifiutate.
     *
     * @param offerId L'ID dell'offerta da accettare.
     * @return L'{@link OfferDetailsDTO} dell'offerta aggiornata allo stato "Accepted".
     */
    @Transactional
    public OfferDetailsDTO acceptOffer(Integer offerId) {
        Offer offerToAccept = findOfferAndVerifyState(offerId);
        
        OfferState acceptedState = offerStatusRepository.findById(ACCEPTED_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Accepted' non trovato nel database."));
        
        offerToAccept.setOfferState(acceptedState);
        Offer updatedOffer = offerRepository.save(offerToAccept);

        declineOtherPendingOffers(updatedOffer);

        return mapToOfferDetailsDTO(updatedOffer);
    }

    /**
     * Rifiuta un'offerta, aggiornando il suo stato a "Declined".
     *
     * @param offerId L'ID dell'offerta da rifiutare.
     * @return L'{@link OfferDetailsDTO} dell'offerta aggiornata allo stato "Declined".
     */
    @Transactional
    public OfferDetailsDTO declineOffer(Integer offerId) {
        Offer offerToDecline = findOfferAndVerifyState(offerId);

        OfferState declinedState = offerStatusRepository.findById(DECLINED_STATUS_ID)
            .orElseThrow(() -> new IllegalStateException("Stato 'Declined' non trovato nel database."));
        
        offerToDecline.setOfferState(declinedState);
        Offer updatedOffer = offerRepository.save(offerToDecline);
        
        return mapToOfferDetailsDTO(updatedOffer);
    }

    /** 
     * Metodo helper per trovare un'offerta e verificare che sia in stato 'Pending'.
     * Centralizza la logica di controllo per evitare duplicazione di codice in `acceptOffer` e `declineOffer`.
     * @param offerId L'ID dell'offerta da cercare.
     * @return L'entità {@link Offer} se trovata e valida.
     * @throws ResourceNotFoundException se l'offerta non esiste.
     * @throws IllegalStateException se l'offerta non è in stato "Pending".
     */
    private Offer findOfferAndVerifyState(Integer offerId) {
        Offer offer = offerRepository.findById(offerId)
            .orElseThrow(() -> new ResourceNotFoundException("Offerta non trovata con ID: " + offerId));

        if (!offer.getOfferState().getId().equals(PENDING_STATUS_ID)) {
            throw new IllegalStateException("È possibile modificare solo offerte con stato 'Pending'.");
        }
        return offer;
    }

    /**
    * Metodo helper che implementa la logica di business per rifiutare tutte le altre offerte
    * in sospeso per una proprietà quando una di esse viene accettata.
    * @param acceptedOffer L'offerta che è stata appena accettata.
    */
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