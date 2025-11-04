package com.dietiestates2025.dieti.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.dozer.DozerBeanMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.dietiestates2025.dieti.dto.DashboardDTO;
import com.dietiestates2025.dieti.dto.PropertyDTO;
import com.dietiestates2025.dieti.model.Address;
import com.dietiestates2025.dieti.model.Dashboard;
import com.dietiestates2025.dieti.model.Image;
import com.dietiestates2025.dieti.model.Property;
import com.dietiestates2025.dieti.model.PropertyStats;
import com.dietiestates2025.dieti.model.SaleType;
import com.dietiestates2025.dieti.model.User;
import com.dietiestates2025.dieti.repositories.DashboardRepository;
import com.dietiestates2025.dieti.repositories.PropertyRepository;
import com.dietiestates2025.dieti.repositories.UserRepository;
import com.dietiestates2025.dieti.repositories.SaleTypeRepository;
import com.dietiestates2025.dieti.exception.*;
import org.springframework.transaction.annotation.Transactional;
import com.dietiestates2025.dieti.model.PropertyState;
import com.dietiestates2025.dieti.repositories.PropertyStateRepository;

@Service
public class PropertyService {

    private final PropertyRepository repo;
    private final AddressService addressService;
    private final DozerBeanMapper dozerBeanMapper;
    private final FileStorageService fileStorageService; 
    private final UserRepository userRepository; 
    private final DashboardRepository dashboardRepository;
    private final SaleTypeRepository saleTypeRepository; // <-- Dipendenza per SaleType
    private final PropertyStateRepository propertyStateRepository; // <-- NUOVA DIPENDENZA

    private static final Integer DEFAULT_PROPERTY_STATE_ID = 1;

    // --- COSTRUTTORE (già corretto) ---
    public PropertyService(
        SaleTypeRepository saleTypeRepository,
        PropertyRepository repo, 
        AddressService addressService, 
        DozerBeanMapper dozerBeanMapper,
        FileStorageService fileStorageService,
        UserRepository userRepository,
        DashboardRepository dashboardRepository,
        PropertyStateRepository propertyStateRepository
    ) {
        this.saleTypeRepository = saleTypeRepository;
        this.repo = repo;
        this.addressService = addressService;
        this.dozerBeanMapper = dozerBeanMapper;
        this.fileStorageService = fileStorageService;
        this.userRepository = userRepository;
        this.dashboardRepository = dashboardRepository;
        this.propertyStateRepository = propertyStateRepository;
    }

    @Transactional
    public PropertyDTO addPropertyAndImages(PropertyDTO propertyDTO, List<MultipartFile> imageFiles, String userEmail){ 
        User owner = userRepository.findById(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + userEmail));

        Dashboard userDashboard = dashboardRepository.findById(owner.getEmail()).orElseGet(() -> {
            Dashboard newDashboard = Dashboard.builder().email(owner.getEmail()).user(owner).build();
            return dashboardRepository.save(newDashboard);
        });
        
        Address address = dozerBeanMapper.map(propertyDTO.getAddress(), Address.class);
        Property property = dozerBeanMapper.map(propertyDTO, Property.class);
        property.setAddress(addressService.checkIfAddressExist(address));
        
        // --- INIZIO LOGICA CORRETTA PER SALETYPE ---

        // 1. Ottieni il nome del tipo di vendita (es. "rent" o "buy") dal DTO.
        //    Assicurati che il tuo PropertyDTO abbia un campo String `saleType`.
        String saleTypeName = propertyDTO.getSaleType();
        if (saleTypeName == null || saleTypeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Sale type ('rent' or 'buy') is required.");
        }

        // 2. Cerca l'entità SaleType nel database usando il nome come ID.
        SaleType saleType = saleTypeRepository.findById(saleTypeName)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "SaleType '" + saleTypeName + "' not found. Make sure it exists in the database."
                ));

        List<SaleType> saleTypes = new ArrayList<>();
        saleTypes.add(saleType);
        property.setSaleTypes(saleTypes);

        // --- INIZIO NUOVA LOGICA PER PROPERTY STATE ---
        // Quando una nuova proprietà viene creata, il suo stato di default è "Available".
        PropertyState defaultState = propertyStateRepository.findById(DEFAULT_PROPERTY_STATE_ID)
            .orElseThrow(() -> new IllegalStateException(
                "Stato di default 'Available' non trovato. Assicurati che esista con ID " + DEFAULT_PROPERTY_STATE_ID
            ));
        
        property.setPropertyState(defaultState);
        // --- FINE NUOVA LOGICA ---
        
        List<Dashboard> dashboards = new ArrayList<>();
        dashboards.add(userDashboard);
        property.setDashboards(dashboards);
        // Salva la proprietà con tutte le sue relazioni (indirizzo, saleType, dashboard)
        Property savedProperty = repo.save(property);

        // Gestione delle immagini (invariata)
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Image> images = imageFiles.stream().map(file -> {
                String fileName = fileStorageService.storeFile(file);
                return new Image(fileName, savedProperty);
            }).collect(Collectors.toList());
            savedProperty.getImages().addAll(images);
        }
        
        Property finalProperty = repo.save(savedProperty);
        return mapToDtoWithImageUrls(finalProperty);
    }
    
    // --- IL RESTO DELLA CLASSE È INVARIATO ---

    public boolean deleteProperty(int propertyId) {
        if(repo.existsById(propertyId)){
            repo.deleteById(propertyId);
            return true;
        }
        return false;
    }

    public PropertyDTO getPropertyById(int propertyId) {
        Property property = repo.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        return mapToDtoWithImageUrls(property);
    }

    public List<PropertyDTO> findPropertiesByLocation(String location) {
        List<Property> properties = repo.findByLocationIgnoreCase(location);
        return properties.stream()
                         .map(this::mapToDtoWithImageUrls)
                         .collect(Collectors.toList());
    }

    private static final Integer AVAILABLE_STATE_ID = 1;
    public List<PropertyDTO> findPropertiesByLocationAvailable(String location) {
        List<Property> properties = repo.findByLocationAndState(location, AVAILABLE_STATE_ID);
        
        return properties.stream()
                         .map(this::mapToDtoWithImageUrls)
                         .collect(Collectors.toList());
    }

    private PropertyDTO mapToDtoWithImageUrls(Property property) {
        PropertyDTO dto = dozerBeanMapper.map(property, PropertyDTO.class);
        
        // --- MAPPIAMO ANCHE SALETYPE PER INVIARLO AL FRONTEND ---
        // Se la lista non è vuota, prendiamo il primo (e unico) elemento
        if (property.getSaleTypes() != null && !property.getSaleTypes().isEmpty()) {
            dto.setSaleType(property.getSaleTypes().get(0).getSaleType());
        }

        // --- NUOVA LOGICA DI MAPPING PER LO STATO ---
        if (property.getPropertyState() != null) {
            dto.setPropertyState(property.getPropertyState().getState());
        }
        // --- FINE NUOVA LOGICA ---

        List<String> imageUrls = property.getImages().stream()
            .map(image -> ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/files/")
                    .path(image.getFileName())
                    .toUriString())
            .collect(Collectors.toList());
            
        dto.setImageUrls(imageUrls);
        return dto;
    }
    @Transactional
    public void incrementViewCount(Integer propertyId) {
        Property property = repo.findById(propertyId)
            .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        PropertyStats stats = property.getPropertyStats();
        if (stats == null) {
            // Se non esistono statistiche, creale
            stats = new PropertyStats();
            stats.setProperty(property);
            stats.setNumberOfViews(1);
        } else {
            // Incrementa il contatore
            stats.setNumberOfViews(stats.getNumberOfViews() + 1);
        }
        
        property.setPropertyStats(stats);
        repo.save(property);
    }

    //  // --- METODO 'getDashboardDataForAgent' AGGIORNATO ---
    // @Transactional(readOnly = true)
    // public DashboardDTO getDashboardDataForAgent(String agentEmail) {
    //     // 1. Cerca il dashboard dell'agente usando l'email come ID.
    //     Optional<Dashboard> dashboardOpt = dashboardRepository.findById(agentEmail);

    //     // 2. Se l'agente non ha ancora un dashboard (es. non ha ancora inserito proprietà),
    //     //    restituisci un DTO vuoto per evitare errori nel frontend.
    //     if (dashboardOpt.isEmpty()) {
    //         return buildEmptyDashboardDTO();
    //     }

    //     // 3. Se il dashboard esiste, ottieni la lista delle proprietà direttamente da esso.
    //     List<Property> properties = dashboardOpt.get().getProperties();

    //     // 4. Da qui in poi, la logica è identica a prima: calcola le statistiche
    //     //    sulla base della lista di proprietà ottenuta.
    //     long totalViews = properties.stream()
    //         .map(Property::getPropertyStats)
    //         .filter(stats -> stats != null)
    //         .mapToLong(PropertyStats::getNumberOfViews)
    //         .sum();

    //     long bookedVisits = properties.stream()
    //         .map(Property::getPropertyStats)
    //         .filter(stats -> stats != null)
    //         .mapToLong(PropertyStats::getNumberOfScheduledVisits)
    //         .sum();
            
    //     long offersReceived = 0; // Placeholder

    //     long activeListings = properties.size();

    //     Map<String, Integer> salesOverTime = new LinkedHashMap<>();
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM");
    //     for (int i = 5; i >= 0; i--) {
    //         String month = LocalDate.now().minusMonths(i).format(formatter);
    //         int sales = (int) (Math.random() * 5) + 1; 
    //         salesOverTime.put(month, sales);
    //     }

    //     List<PropertyDTO> propertyDTOs = properties.stream()
    //             .map(this::mapToDtoWithImageUrls)
    //             .collect(Collectors.toList());

    //     return DashboardDTO.builder()
    //             .totalViews(totalViews)
    //             .bookedVisits(bookedVisits)
    //             .offersReceived(offersReceived)
    //             .activeListings(activeListings)
    //             .properties(propertyDTOs)
    //             .salesOverTime(salesOverTime)
    //             .build();
    // }
    
    // // Helper method per creare un DTO vuoto
    // private DashboardDTO buildEmptyDashboardDTO() {
    //     return DashboardDTO.builder()
    //         .totalViews(0)
    //         .bookedVisits(0)
    //         .offersReceived(0)
    //         .activeListings(0)
    //         .properties(Collections.emptyList())
    //         .salesOverTime(new LinkedHashMap<>())
    //         .build();
    // }
}