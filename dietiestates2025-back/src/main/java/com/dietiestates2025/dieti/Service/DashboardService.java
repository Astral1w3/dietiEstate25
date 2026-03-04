package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.DashboardDTO;
import com.dietiestates2025.dieti.dto.PropertyDashboardDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.*;
import com.dietiestates2025.dieti.repositories.BookedVisitRepository;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final BookedVisitRepository bookedVisitRepository;
    private final OfferRepository offerRepository;

    public DashboardService(DashboardRepository dashboardRepository,
                            BookedVisitRepository bookedVisitRepository,
                            OfferRepository offerRepository) {
        this.dashboardRepository = dashboardRepository;
        this.bookedVisitRepository = bookedVisitRepository;
        this.offerRepository = offerRepository;
    }

    /**
     * Recupera e assembla tutti i dati necessari per la dashboard di un agente specifico.
     *
     * @param agentEmail L'email dell'agente per cui recuperare i dati.
     * @return Un {@link DashboardDTO} contenente tutte le statistiche aggregate e la lista delle proprietà.
     * @throws ResourceNotFoundException se non viene trovata una dashboard per l'agente specificato.
     */
    @Transactional(readOnly = true) // Ottimizzazione per operazioni di sola lettura
    public DashboardDTO getDashboardDataForAgent(String agentEmail) {
        // 1. Recupera la dashboard e tutte le proprietà associate con una singola query ottimizzata.
        Dashboard agentDashboard = dashboardRepository.findByEmailWithFullProperties(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovata per l'agente: " + agentEmail));

        // Le query con JOIN multiple possono restituire righe duplicate per la stessa entità.
        // Utilizziamo un HashSet per rimuovere efficientemente le proprietà duplicate prima di procedere.
        List<Property> properties = new ArrayList<>(new HashSet<>(agentDashboard.getProperties()));

        // Se l'agente non ha annunci attivi, restituisce una dashboard vuota.
        if (properties.isEmpty()) {
            return buildEmptyDashboardDTO();
        }

        // 2. Estrae gli ID di tutte le proprietà uniche.
        List<Integer> propertyIds = properties.stream()
                .map(Property::getIdProperty)
                .collect(Collectors.toList());

        // 3. Esegue query batch per ottenere i conteggi aggregati, evitando il problema N+1.
        // Recupera il conteggio delle visite per tutte le proprietà in una sola chiamata.
        Map<Integer, Long> visitsCountMap = bookedVisitRepository.countVisitsByPropertyIdsGrouped(propertyIds)
                .stream()
                .collect(Collectors.toMap(BookedVisitRepository.PropertyCount::getPropertyId, BookedVisitRepository.PropertyCount::getCount));

        // Recupera il conteggio delle offerte per tutte le proprietà in una sola chiamata.
        Map<Integer, Long> offersCountMap = offerRepository.countOffersByPropertyIdsGrouped(propertyIds)
                .stream()
                .collect(Collectors.toMap(OfferRepository.PropertyCount::getPropertyId, OfferRepository.PropertyCount::getCount));

        // 4. Calcola le statistiche totali aggregando i dati ottenuti.
        long totalViews = properties.stream()
            .map(Property::getPropertyStats)
            .filter(Objects::nonNull)
            .mapToLong(PropertyStats::getNumberOfViews)
            .sum();

        long totalBookedVisits = visitsCountMap.values().stream().mapToLong(Long::longValue).sum();
        long totalOffersReceived = offersCountMap.values().stream().mapToLong(Long::longValue).sum();

        // 5. Mappa ogni entità Property a un DTO specifico per la dashboard, arricchendolo con i conteggi.
        List<PropertyDashboardDTO> propertyDTOs = properties.stream()
                .map(p -> mapToPropertyDashboardDTO(p, visitsCountMap, offersCountMap))
                .collect(Collectors.toList());

        // 6. Costruisce e restituisce il DTO finale della dashboard.
        return DashboardDTO.builder()
                .totalViews(totalViews)
                .bookedVisits(totalBookedVisits)
                .offersReceived(totalOffersReceived)
                .activeListings(properties.size())
                .properties(propertyDTOs)
                .build();
    }
    
    /**
     * Metodo helper per mappare una singola entità {@link Property} a un {@link PropertyDashboardDTO}.
     * Arricchisce il DTO con dati aggregati come il conteggio delle visite e delle offerte.
     *
     * @param p L'entità Property da mappare.
     * @param visitsCountMap Una mappa pre-calcolata [PropertyID -> Conteggio Visite] per un recupero efficiente.
     * @param offersCountMap Una mappa pre-calcolata [PropertyID -> Conteggio Offerte] per un recupero efficiente.
     * @return Il DTO della proprietà, pronto per essere visualizzato nella dashboard.
     */
    private PropertyDashboardDTO mapToPropertyDashboardDTO(Property p, Map<Integer, Long> visitsCountMap, Map<Integer, Long> offersCountMap) {
        String fullAddress = (p.getAddress() != null && p.getAddress().getMunicipality() != null)
            ? p.getAddress().getStreet() + ", " + p.getAddress().getMunicipality().getMunicipalityName()
            : "Indirizzo non disponibile";

        Integer propertyId = p.getIdProperty();
        
        // Costruisce un URL assoluto per l'immagine principale, rendendolo utilizzabile dal frontend.
        String mainImageUrl = p.getImages().stream()
                .findFirst()
                .map(image -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/files/")
                        .path(image.getFileName())
                        .toUriString())
                .orElse(null); // Restituisce null se non ci sono immagini.

        String saleTypeString = p.getSaleTypes().stream()
                .findFirst()
                .map(SaleType::getSaleType)
                .orElse("N/A"); // Valore di default se non è specificato un tipo di vendita.

        return PropertyDashboardDTO.builder()
                .idProperty(propertyId)
                .fullAddress(fullAddress)
                .saleType(saleTypeString)
                .propertyState(p.getPropertyState() != null ? p.getPropertyState().getState() : "N/A")
                .price(p.getPrice())
                .mainImageUrl(mainImageUrl)
                .viewCount(p.getPropertyStats() != null ? p.getPropertyStats().getNumberOfViews() : 0)
                .bookedVisitsCount(visitsCountMap.getOrDefault(propertyId, 0L)) // Usa getOrDefault per sicurezza
                .offersCount(offersCountMap.getOrDefault(propertyId, 0L))
                .build();
    }

    /**
     * Costruisce un {@link DashboardDTO} vuoto con valori di default.
     * Utilizzato per fornire una risposta consistente quando un agente non ha proprietà attive.
     * @return Un DTO vuoto ma inizializzato.
     */
    private DashboardDTO buildEmptyDashboardDTO() {
        return DashboardDTO.builder()
            .totalViews(0)
            .bookedVisits(0)
            .offersReceived(0)
            .activeListings(0)
            .properties(Collections.emptyList())
            .salesOverTime(new LinkedHashMap<>()) // Inizializza la mappa per evitare NullPointerException
            .build();
    }
}