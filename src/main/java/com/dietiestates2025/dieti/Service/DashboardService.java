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

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardDataForAgent(String agentEmail) {
        
        Dashboard agentDashboard = dashboardRepository.findByEmailWithFullProperties(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovato per l'agente: " + agentEmail));

        Set<Property> uniqueProperties = new HashSet<>(agentDashboard.getProperties());
    
        // Da qui in poi, lavora con la collezione di proprietà uniche.
        if (uniqueProperties.isEmpty()) {
            return buildEmptyDashboardDTO();
        }

        // Trasformiamo il Set in una List per le operazioni successive che la richiedono
        List<Property> properties = new ArrayList<>(uniqueProperties);
        
        if (properties == null || properties.isEmpty()) {
            return buildEmptyDashboardDTO();
        }

        long totalViews = properties.stream()
            .map(Property::getPropertyStats)
            .filter(Objects::nonNull)
            .mapToLong(PropertyStats::getNumberOfViews)
            .sum();

        List<Integer> propertyIds = properties.stream().map(Property::getIdProperty).collect(Collectors.toList());
        
        long totalBookedVisits = bookedVisitRepository.countByPropertyIds(propertyIds);
        long totalOffersReceived = offerRepository.countByPropertyIds(propertyIds);
        
        long activeListings = properties.size();
        Map<String, Long> salesOverTime = generateMockSalesData();

        List<PropertyDashboardDTO> propertyDTOs = properties.stream()
                .map(this::mapToPropertyDashboardDTO) // Ora questa riga è di nuovo valida
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalViews(totalViews)
                .bookedVisits(totalBookedVisits)
                .offersReceived(totalOffersReceived)
                .activeListings(activeListings)
                .salesOverTime(salesOverTime)
                .properties(propertyDTOs)
                .build();
    }

    // --- METODO MANCANTE REINSERITO QUI ---
    /**
     * Metodo helper per mappare una singola entità Property al suo DTO per la dashboard.
     */
    private PropertyDashboardDTO mapToPropertyDashboardDTO(Property p) {
        String fullAddress = p.getAddress() != null && p.getAddress().getMunicipality() != null
            ? p.getAddress().getStreet() + ", " + p.getAddress().getMunicipality().getMunicipalityName()
            : "Indirizzo non disponibile";
            
        Integer propertyId = p.getIdProperty();

        String mainImageUrl = null;
        Set<Image> images = p.getImages();
        if (images != null && !images.isEmpty()) {
            Image firstImage = images.iterator().next(); 
            mainImageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(firstImage.getFileName())
                    .toUriString();
        }

        String saleTypeString = "N/A";
        Set<SaleType> saleTypes = p.getSaleTypes();
        if (saleTypes != null && !saleTypes.isEmpty()) {
            saleTypeString = saleTypes.iterator().next().getSaleType();
        }

        return PropertyDashboardDTO.builder()
                .idProperty(propertyId)
                .fullAddress(fullAddress)
                .saleType(saleTypeString)
                .propertyState(p.getPropertyState() != null ? p.getPropertyState().getState() : "N/A")
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