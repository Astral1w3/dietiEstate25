package com.dietiestates2025.dieti.Service;

import com.dietiestates2025.dieti.dto.DashboardDTO;
import com.dietiestates2025.dieti.dto.PropertyDashboardDTO;
import com.dietiestates2025.dieti.exception.ResourceNotFoundException;
import com.dietiestates2025.dieti.model.Dashboard;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.PropertyStats;
import com.dietiestates2025.dieti.repositories.BookedVisitRepository;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.OfferRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;
    private final BookedVisitRepository bookedVisitRepository;
    private final OfferRepository offerRepository;

    // Inietta tutti i repository necessari per l'aggregazione dei dati
    public DashboardService(DashboardRepository dashboardRepository,
                            BookedVisitRepository bookedVisitRepository,
                            OfferRepository offerRepository) {
        this.dashboardRepository = dashboardRepository;
        this.bookedVisitRepository = bookedVisitRepository;
        this.offerRepository = offerRepository;
    }

    /**
     * Aggrega tutti i dati necessari per la dashboard di un agente.
     * @param agentEmail L'email dell'agente autenticato.
     * @return Un DTO completo con tutte le statistiche.
     */
    @Transactional(readOnly = true)
    public DashboardDTO getDashboardDataForAgent(String agentEmail) {
        // 1. Recupera il dashboard dell'agente e, da lì, la lista delle sue proprietà
        Dashboard agentDashboard = dashboardRepository.findById(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovato per l'agente: " + agentEmail));

        List<Property> properties = agentDashboard.getProperties();
        
        // Se l'agente non ha proprietà, restituisci un DTO vuoto per evitare errori sul frontend
        if (properties == null || properties.isEmpty()) {
            return buildEmptyDashboardDTO();
        }

        // 2. Calcola le statistiche globali per le StatCard
        long totalViews = properties.stream()
            .map(Property::getPropertyStats)
            .filter(Objects::nonNull) // Ignora le proprietà che non hanno ancora statistiche
            .mapToLong(PropertyStats::getNumberOfViews)
            .sum();

        List<Integer> propertyIds = properties.stream().map(Property::getIdProperty).collect(Collectors.toList());
        long totalBookedVisits = bookedVisitRepository.findByPropertyIdPropertyIn(propertyIds).size();
        long totalOffersReceived = offerRepository.findByPropertyIdPropertyIn(propertyIds).size();
        long activeListings = properties.size();

        // 3. Genera dati simulati per il grafico "vendite nel tempo"
        Map<String, Long> salesOverTime = generateMockSalesData();

        // 4. Prepara la lista dettagliata di ogni proprietà per la tabella
        List<PropertyDashboardDTO> propertyDTOs = properties.stream()
                .map(this::mapToPropertyDashboardDTO)
                .collect(Collectors.toList());

        // 5. Costruisci e restituisci il DTO finale che contiene tutto
        return DashboardDTO.builder()
                .totalViews(totalViews)
                .bookedVisits(totalBookedVisits)
                .offersReceived(totalOffersReceived)
                .activeListings(activeListings)
                .salesOverTime(salesOverTime)
                .properties(propertyDTOs)
                .build();
    }

    /**
     * Metodo helper per mappare una singola entità Property al suo DTO per la dashboard.
     * Utilizza i metodi del repository basati su ID per i conteggi, come da tua correzione.
     */
    private PropertyDashboardDTO mapToPropertyDashboardDTO(Property p) {
        String fullAddress = p.getAddress().getStreet() + ", " + p.getAddress().getMunicipality().getMunicipalityName();
        Integer propertyId = p.getIdProperty();

        String mainImageUrl = null;
        if (p.getImages() != null && !p.getImages().isEmpty()) {
            // Prendiamo la prima immagine come immagine principale
            Image firstImage = p.getImages().get(0);
            mainImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(firstImage.getFileName())
                    .toUriString();
        }

        return PropertyDashboardDTO.builder()
                .idProperty(propertyId)
                .fullAddress(fullAddress)
                .saleType(p.getSaleTypes().isEmpty() ? "N/A" : p.getSaleTypes().get(0).getSaleType())
                .propertyState(p.getPropertyState().getState())
                .price(p.getPrice())
                .mainImageUrl(mainImageUrl)
                .viewCount(p.getPropertyStats() != null ? p.getPropertyStats().getNumberOfViews() : 0)
                .bookedVisitsCount(bookedVisitRepository.countByPropertyIdProperty(propertyId))
                .offersCount(offerRepository.countByPropertyIdProperty(propertyId))
                .build();
    }

    /** Helper per restituire un DTO vuoto e valido. */
    private DashboardDTO buildEmptyDashboardDTO() {
        return DashboardDTO.builder()
            .totalViews(0)
            .bookedVisits(0)
            .offersReceived(0)
            .activeListings(0)
            .properties(Collections.emptyList())
            .salesOverTime(new LinkedHashMap<>())
            .build();
    }

    /** Helper per generare dati simulati per il grafico. */
    private Map<String, Long> generateMockSalesData() {
        Map<String, Long> salesOverTime = new LinkedHashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ITALIAN);
        for (int i = 5; i >= 0; i--) {
            String month = LocalDate.now().minusMonths(i).format(formatter);
            long sales = (long) (Math.random() * 5); // Valori casuali
            salesOverTime.put(month, sales);
        }
        return salesOverTime;
    }
}