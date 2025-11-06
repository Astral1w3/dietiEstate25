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
  @Transactional(readOnly = true)
    public DashboardDTO getDashboardDataForAgent(String agentEmail) {
        Dashboard agentDashboard = dashboardRepository.findByEmailWithFullProperties(agentEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Dashboard non trovata per l'agente: " + agentEmail));

        // --- LA CORREZIONE È QUI ---
        // 1. Prendiamo la lista originale che può contenere duplicati.
        // 2. La inseriamo in un new HashSet() per rimuovere automaticamente i duplicati.
        // 3. Creiamo una nuova ArrayList a partire dal Set de-duplicato.
        List<Property> properties = new ArrayList<>(new HashSet<>(agentDashboard.getProperties()));

        if (properties.isEmpty()) {
            return buildEmptyDashboardDTO();
        }

        List<Integer> propertyIds = properties.stream()
                .map(Property::getIdProperty)
                .collect(Collectors.toList());

        // Il resto della logica di ottimizzazione rimane invariato e corretto
        Map<Integer, Long> visitsCountMap = bookedVisitRepository.countVisitsByPropertyIdsGrouped(propertyIds)
                .stream()
                .collect(Collectors.toMap(BookedVisitRepository.PropertyCount::getPropertyId, BookedVisitRepository.PropertyCount::getCount));

        Map<Integer, Long> offersCountMap = offerRepository.countOffersByPropertyIdsGrouped(propertyIds)
                .stream()
                .collect(Collectors.toMap(OfferRepository.PropertyCount::getPropertyId, OfferRepository.PropertyCount::getCount));

        long totalViews = properties.stream()
            .map(Property::getPropertyStats)
            .filter(Objects::nonNull)
            .mapToLong(PropertyStats::getNumberOfViews)
            .sum();

        long totalBookedVisits = visitsCountMap.values().stream().mapToLong(Long::longValue).sum();
        long totalOffersReceived = offersCountMap.values().stream().mapToLong(Long::longValue).sum();

        List<PropertyDashboardDTO> propertyDTOs = properties.stream()
                .map(p -> mapToPropertyDashboardDTO(p, visitsCountMap, offersCountMap))
                .collect(Collectors.toList());

        return DashboardDTO.builder()
                .totalViews(totalViews)
                .bookedVisits(totalBookedVisits)
                .offersReceived(totalOffersReceived)
                .activeListings(properties.size())
                .properties(propertyDTOs)
                .build();
    }
    
    private PropertyDashboardDTO mapToPropertyDashboardDTO(Property p, Map<Integer, Long> visitsCountMap, Map<Integer, Long> offersCountMap) {
        String fullAddress = (p.getAddress() != null && p.getAddress().getMunicipality() != null)
            ? p.getAddress().getStreet() + ", " + p.getAddress().getMunicipality().getMunicipalityName()
            : "Indirizzo non disponibile";

        Integer propertyId = p.getIdProperty();
        
        String mainImageUrl = p.getImages().stream()
                .findFirst()
                .map(image -> ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/api/files/")
                        .path(image.getFileName())
                        .toUriString())
                .orElse(null);

        String saleTypeString = p.getSaleTypes().stream()
                .findFirst()
                .map(SaleType::getSaleType)
                .orElse("N/A");

        return PropertyDashboardDTO.builder()
                .idProperty(propertyId)
                .fullAddress(fullAddress)
                .saleType(saleTypeString)
                .propertyState(p.getPropertyState() != null ? p.getPropertyState().getState() : "N/A")
                .price(p.getPrice())
                .mainImageUrl(mainImageUrl)
                .viewCount(p.getPropertyStats() != null ? p.getPropertyStats().getNumberOfViews() : 0)
                .bookedVisitsCount(visitsCountMap.getOrDefault(propertyId, 0L))
                .offersCount(offersCountMap.getOrDefault(propertyId, 0L))
                .build();
    }

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
}